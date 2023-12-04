package lk.ijse.dep11.edupanel.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureresTO {
    private Integer id;
    private String name;
   private String designation;
   private String qualificatons;
    private String type;
   private String pictureUrl;
   private String linkediprivaten;
}
