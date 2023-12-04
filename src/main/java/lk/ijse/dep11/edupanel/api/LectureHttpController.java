package lk.ijse.dep11.edupanel.api;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lk.ijse.dep11.edupanel.to.request.LectureReqTO;
import lk.ijse.dep11.edupanel.to.response.LectureresTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.annotation.MultipartConfig;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/lectures")
@CrossOrigin
public class LectureHttpController {
    @Autowired
    private DataSource pool;
    @Autowired
    private Bucket bucket;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public LectureresTO createNewLecture(@ModelAttribute @Valid LectureReqTO lecturer ){

        try (Connection connection = pool.getConnection()){

            connection.setAutoCommit(false);
            try{
                PreparedStatement stmInsert = connection.prepareStatement("INSERT INTO lecturer ( name, designation, qualifications, linkedin) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                stmInsert.setString(1,lecturer.getName());
                stmInsert.setString(2,lecturer.getDesignation());
                stmInsert.setString(3,lecturer.getQualifications());
                stmInsert.setString(4,lecturer.getLinkedin());
                stmInsert.executeUpdate();
                ResultSet generatedKeys = stmInsert.getGeneratedKeys();
                generatedKeys.next();

                int lectureId = generatedKeys.getInt(1);

                String picture= lectureId+"-"+lecturer.getName();

                if(lecturer.getPicture() !=null || !lecturer.getPicture().isEmpty()){

                    PreparedStatement stmUpdateLecture = connection.prepareStatement("UPDATE lecturer SET picture = ? WHERE id= ?");
                    stmUpdateLecture.setString(1,picture);
                    stmUpdateLecture.setInt(2,lectureId);
                    stmUpdateLecture.executeUpdate();

                }
                final String table = lecturer.getType().equalsIgnoreCase("full-time")? "full_time_rank": "part_time_rank";

                    Statement stm = connection.createStatement();

                    ResultSet rst = stm.executeQuery("SELECT `rank` FROM " + table + " ORDER BY `rank` DESC LIMIT 1");

                    int rank;
                    if(!rst.next())rank=1;
                    else{
                        rank =rst.getInt("rank")+1;
                    }

                    PreparedStatement stmInsertRank = connection.prepareStatement("INSERT INTO "+table+" (lecture_id, `rank`) VALUES (?,?)");
                    stmInsertRank.setInt(1,lectureId);
                    stmInsertRank.setInt(2,rank);
                    stmInsertRank.executeUpdate();
                    String pictureUrl = null;
                if(lecturer.getPicture() !=null || !lecturer.getPicture().isEmpty()){
                    Blob blob = bucket.create(picture, lecturer.getPicture().getInputStream(), lecturer.getPicture().getContentType());
                    pictureUrl = blob.signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString();
                }

                connection.commit();
                return new LectureresTO(lectureId, lecturer.getName(),lecturer.getDesignation(),lecturer.getQualifications(),lecturer.getType(),pictureUrl,lecturer.getLinkedin());
            }catch(Throwable t){
                connection.rollback();
                throw  t;
            }finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PatchMapping("/{lecturer-id}")
    public void updateLectureDetails(){
        System.out.println("update");
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lecturer-id}")
    public void deleteLecture(@PathVariable("lecturer-id") int lecturerId){
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stmExist = connection.prepareStatement("SELECT *  FROM lecturer WHERE id =?");
            stmExist.setInt(1,lecturerId);
            if(!stmExist.executeQuery().next()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            connection.setAutoCommit(false);
            try {

                PreparedStatement stmIdentify = connection.prepareStatement("SELECT l.id, l.name, l.picture, ftr.`rank` AS ftr, ptr.`rank` AS ptr FROM lecturer l left outer join full_time_rank ftr on l.id = ftr.lecture_id LEFT OUTER JOIN part_time_rank ptr on l.id = ptr.lecture_id WHERE l.id = ?");
                stmIdentify.setInt(1,lecturerId);
                ResultSet rst = stmIdentify.executeQuery();
                rst.next();
                int ftr = rst.getInt("ftr");
                int ptr = rst.getInt("ptr");
                String picure = rst.getString("picture");
                 String table = ftr > 0? "full_time_rank": "part_time_rank";
                 int rank = ftr>0? ftr: ptr;
                Statement stmDeleteRank = connection.createStatement();
                stmDeleteRank.executeUpdate("DELETE FROM " + table + " WHERE `rank` = " + rank);
                 Statement stmShift =   connection.createStatement();
                 stmShift.executeUpdate("UPDATE "+table +" SET `rank` = `rank` - 1 WHERE `rank` > "+rank);
                PreparedStatement stmDelete = connection.prepareStatement("DELETE FROM lecturer WHERE id = ?");
                stmDelete.setInt(1,lecturerId);
                stmDelete.executeUpdate();

                if(picure!=null){
                    bucket.get(picure).delete();
                }


                connection.commit();
            }catch (Throwable t){
                connection.rollback();
                throw t;
            }finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping
    public void getAllLectures(){
        System.out.println("getall");
    }
}
