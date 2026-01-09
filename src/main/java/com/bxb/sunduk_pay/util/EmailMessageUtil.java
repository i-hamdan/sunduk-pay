package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import org.springframework.stereotype.Component;

/**
 * Utility component for building email subjects and bodies
 * for different user and goal events.
 */
@Component
public final class EmailMessageUtil {
    /**
     * Milestone constants.
     */
    private static final int MILESTONE_50 = 50;
    /**
     * Milestone constants.
     */
    private static final int MILESTONE_75 = 75;
    /**
     * Milestone constants.
     */
    private static final int MILESTONE_100 = 100;

    /**
     * Build the subject line for a user event.
     *
     * @param event the user Kafka event
     * @return the subject line
     */
    public String buildSubject(final UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Login Alert - Welcome back to Sunduk, "
                    + event.getFullName() + "!";
        } else {
            return "Welcome to Sunduk family "
                    + event.getFullName() + "!";
        }
    }

    /**
     * Build the body content for a user event.
     *
     * @param event the user Kafka event
     * @return the body content
     */
    public String buildBody(final UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "Hi " + event.getFullName() + ",\n\n"
                    + "We're happy to see you back on Sunduk!\n"
                    + "You have successfully logged in to your account.\n\n"
                    + "If this wasn't you, please "
                    + "secure your account immediately.\n\n"
                    + "Team Sunduk";
        } else {
            return "Hi " + event.getFullName() + ",\n\n"
                    + "Welcome to SundukPay! \n\n"
                    + "Your account has been successfully created, "
                    + "and you’re now part of a secure and seamless way "
                    + "to manage your money.\n\n"
                    + "Here’s what you can do with SundukPay:\n"
                    + "• Add and manage funds with ease\n"
                    + "• Create Saving Pots to "
                    + "set goals and track your progress\n"
                    + "• Deposit or withdraw money from your pots anytime\n"
                    + "• Transfer funds flexibly : "
                    + "pot ↔ wallet, and even pot ↔ pot\n"
                    + "• Make safe payments and monitor all wallet activity in "
                    + "real-time\n\n"
                    + "Start exploring today and "
                    + "take control of your finances like "
                    + "never before!\n\n"
                    + "If you ever need assistance "
                    + "our support team is always ready "
                    + "to help.\n\n"
                    + "Thank you for choosing SundukPay,\n "
                    + "We’re excited to see you "
                    + "achieve your financial goals with us!\n\n"
                    + "Warm regards,\n"
                    + "SundukPay Team";
        }
    }

    /**
     * Build the subject line for an OTP event.
     *
     * @param event the OTP event
     * @return the subject line
     */
    public String buildSubjectForOtp(final OtpEvent event) {
        return "Hi " + event.getFullname() + ", Your Sunduk MPIN Reset OTP";
    }

    /**
     * Build the body content for an OTP event.
     *
     * @param event the OTP event
     * @return the body content
     */
    public String buildBodyForOtp(final OtpEvent event) {
        String html = """
                 <!DOCTYPE html>
                 <html lang="en">
                 <head>
                 <meta charset="UTF-8" />
                 <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                 <title>4 Digit PIN To Reset Sunduk MPIN</title>
                 <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
                 rel="stylesheet" />
                 <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap"\s
                 rel="stylesheet" />
                 <style>
                 * {font-family: "Poppins", sans-serif !important;}
                 body { background-color: #f4f4f4; margin: 0; padding: 0; }
                 .card-custom { border-radius: 16px;
                 box-shadow: 0 2px 6px rgba(0,0,0,0.05);}
                 .logo-text { color: #C19945; font-weight: 700; font-size: 20px;}
                 .title-bar {border-top: 1px solid #f0f0f0;
                  border-bottom: 1px solid #f0f0f0; color: #666666;
                 padding: 12px 0; font-weight: 500; text-align: center;
                 font-size: 22px; white-space: nowrap;
                 letter-spacing: 0.5px; }
                 .pin-box { font-size: 22px;
                 color: #C19945; font-weight: bold; letter-spacing: 4px;
                 display: inline-block; padding: 8px 0;}
                 .bracket {color: #585757f7 !important; font-weight: 500;}
                 .security-list {list-style-type: none;
                  padding-left: 0; margin: 0;}
                 .security-list li {margin-bottom: 6px;
                  line-height: 1.8; padding-left: 8px;}
                 .security-list span {color: #C19945;
                 font-weight: 600; margin-right: 6px;}
                 .footer {font-size: 20px; color: #999999;
                 margin-top: 20px; text-align: left;}
                 .greeting {font-size: 20px;margin-bottom: 0.5rem;}
                 .hi-text {color: #000000f7
                 ;font-weight: 400;font-size: 25px; margin-right: 5px;}
                 .name-text { color: #C19945;font-weight: 500;font-size: 20px;}
                 </style>
                 </head>
                 <body>
                 <div class="container my-5">
                 <div class="card card-custom mx-auto" style="max-width: 600px;">
                 <div class="card-body ">
                 <!-- Logo Section -->
                 <div class="d-flex align-items-center " style="display:flex !important;
                 justify-items:center !important;gap:14px; ">
                 <img src="cid:logoImage" alt="Sunduk Pay Logo"
                 width="90" height="90" class="me-2" />
                 <span class="logo-text"
                 style="padding-top:30px !important">Sunduk Pay</span>
                 </div>
                 <!-- Title Bar -->
                 <div class="title-bar mb-4">4 Digit PIN To Reset Sunduk MPIN</div>
                <!-- Content -->
                <p class="greeting mb-2">
                <span class="hi-text">Hi</span>
                <span class="name-text">%s</span>
                </p>
                <p class="text-muted">
                We received a request to
                <strong class="text-dark">reset
                </strong> your PIN for your Sunduk Account.
                </p>
                <p class="text-muted mt-2">
                To proceed, please use the
                <strong class="text-dark">4-digit verification MPIN
                </strong> below to reset your MPIN securely in the Sunduk app.
                </p>
                <h4 class="fw-semibold mt-4 mb-1 text-dark">Your Verification PIN</h4>
                <div class="pin-box">
                <span class="pin"><span class="bracket">[</span>%s </span>
                <span class="bracket">]</span>
                </div>
                <p class="text-muted mt-3">
                This PIN is valid for
                <strong class="text-dark">Today</strong> and can only be used once.<br>
                Enter this code on the
                <strong class="text-dark">“Verify OTP”</strong>
                screen in your Sunduk app to set your new MPIN.
                </p>
                <p class="fw-semibold text-dark mt-3">
                If you did not request this reset,
                please ignore this message. Your account will remain secure.
                </p>
                <!-- Security Tips -->
                <ul class="security-list text-muted">
                <li><span>#</span>Never share this PIN or your MPIN with anyone,
                including Sunduk representatives.</li>
                <li><span>#</span>Sunduk will never ask for your PIN via call,
                SMS, or email.</li>
                <li><span>#</span>Always use the official Sunduk app or website.</li>
                </ul>
                <!-- Help -->
                <h4 class="mt-4 text-dark fw-semibold">Need Help?</h4>
                <p class="text-muted mb-0">
                Contact our support team at
                <a href="mailto:support@sunduk.com" class="text-decoration-none"
                style="color:#C19945;">support@sunduk.com</a>
                </p>
                <!-- Footer -->
                <p class="footer">© 2025 Sunduk Technologies Pvt. Ltd.</p>
                </div>
                </div>
                </div>
                </body>
                </html>""";
        return String.format(html, event.getFullname(), event.getOtp());
    }

}
