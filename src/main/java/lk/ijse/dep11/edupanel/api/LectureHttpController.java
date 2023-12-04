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
                    ResultSet rst = stm.executeQuery("SELECT `rank` FROM"+table+" ORDER BY `rank` DESC LIMIT 1");
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
    @DeleteMapping("/{lecturer-id}")
    public void deleteLecture(){
        System.out.println("delete");
    }
    @GetMapping
    public void getAllLectures(){
        System.out.println("getall");
    }
}
