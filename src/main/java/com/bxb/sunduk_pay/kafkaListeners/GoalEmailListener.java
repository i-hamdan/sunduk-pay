//package com.bxb.sunduk_pay.kafkaListeners;
//
//import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
//import com.bxb.sunduk_pay.service.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * Kafka listener for processing goal completion events and
// * sending notification emails.
// */
//@Component
//@RequiredArgsConstructor
//public class GoalEmailListener {
//
//    /**
//     * Service for handling email operations.
//     */
//    private final EmailService emailService;
//    /**
//     * Listens to the "goal-completion-topic" Kafka topic for
//     * goal completion events.
//     * Processes each event by invoking the email service to
//     * send notification emails.
//     *
//     * @param event the goal completion event received from Kafka
//     */
//    @KafkaListener(topics = "goal-completion-topic",
//            groupId = "email-service-group",
//            concurrency = "3")
//    public void consumeGoalEvent(final GoalCompletionEvent event) {
//        emailService.processGoalCompletionEvent(event);
//    }
//}
//
