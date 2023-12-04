package lk.ijse.dep11.edupanel.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lectures/part-time")
@CrossOrigin
public class PartTimeHttpController {

    @PatchMapping("/ranks")
    public void arrangePartTimeLecturesOrder(){
        System.out.println("arrangeparttimerank");
    }
    @GetMapping
    public void getAllPartTimeLectures(){
        System.out.println("getalaparttime");
    }
}
