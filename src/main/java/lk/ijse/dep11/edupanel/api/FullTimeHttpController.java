package lk.ijse.dep11.edupanel.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lectures/full-time")
@CrossOrigin
public class FullTimeHttpController {
    @PatchMapping("/ranks")
    public void arrangeFullTimeLecturesOrder(){
        System.out.println("arrangefultimerank");
    }
    @GetMapping
    public void getAllFullTimeLectures(){
        System.out.println("getallfullime");
    }
}
