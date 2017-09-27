package com.sorsix.eventscheduler.tasks;

import com.sorsix.eventscheduler.domain.EventAttendance;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.EventAttendanceRepository;
import com.sorsix.eventscheduler.repository.EventRepository;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GenerateReport {


    private JavaMailSender mailSender;
    private EventAttendanceRepository attendanceRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private VelocityEngine velocityEngine;

    public GenerateReport(JavaMailSender mailSender, EventAttendanceRepository attendanceRepository, EventRepository eventRepository, UserRepository userRepository, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.velocityEngine = velocityEngine;
    }

    @Scheduled(cron = "0 * 16 * * ?")
    public void generateOverallReport(){

    }

    @Scheduled(cron = "0 * 19 * * ?")
    public void generateUserReport(){
        System.out.println("In task!cl");
        List<User> usersInLast7Days = userRepository.findAllByDateCreatedBetween(LocalDateTime.now().minusDays(14L), LocalDateTime.now());
        //List<User> usersInLast7Days = userRepository.findAll();

        usersInLast7Days.forEach(user -> {

            List<EventAttendance> goings = attendanceRepository.findAllByUserIdAndDateBetween(user.getId(), LocalDateTime.now().minusDays(14), LocalDateTime.now());
            String [] eventNames = new String[goings.size()];
            for(int i = 0; i < goings.size(); i++){
                eventNames[i] = goings.get(i).getEvent().getName();
            }

            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                if(user.getEmail() != null)
                    message.setTo(user.getEmail());
                else message.setTo("example@test.com");
                message.setFrom("eventscheduler@sorsix.com"); // could be parameterized...
                Map model = new HashMap();
                model.put("user", user);
                model.put("totalGoings", goings.size());
                model.put("eventNames", eventNames);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, "/report.vm", model);
                message.setText(text, true);
            };
            this.mailSender.send(preparator);
        });
    }
}
