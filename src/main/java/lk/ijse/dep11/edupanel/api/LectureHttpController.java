package lk.ijse.dep11.edupanel.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lectures")
@CrossOrigin
public class LectureHttpController {
    @PostMapping
    public   void createNewLecture(){
        System.out.println("create");
    }
    @PatchMapping
    public void updateLectureDetails(){
        System.out.println("update");
    }
    @DeleteMapping
    public void deleteLecture(){
        System.out.println("delete");
    }
    @GetMapping
    public void getAllLectures(){
        System.out.println("getall");
    }
}
