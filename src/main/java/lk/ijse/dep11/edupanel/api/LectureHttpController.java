package lk.ijse.dep11.edupanel.api;

import lk.ijse.dep11.edupanel.to.request.LectureReqTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/lectures")
@CrossOrigin
public class LectureHttpController {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public  void createNewLecture(@ModelAttribute @Valid LectureReqTO lecturer ){
        System.out.println(lecturer);
        System.out.println("create");
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
