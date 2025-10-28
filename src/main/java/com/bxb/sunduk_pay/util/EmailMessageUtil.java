package com.bxb.sunduk_pay.util;


import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import org.springframework.stereotype.Component;

/**
 * Utility component for building email subjects and bodies
 * for different user and goal events.
 */
@Component
public class EmailMessageUtil {
    /** Milestone constants. */
    private static final int MILESTONE_50 = 50;
    /** Milestone constants. */
    private static final int MILESTONE_75 = 75;
    /** Milestone constants. */
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
            return "Assalamualaikum " + event.getFullName() + ",\n\n"
                     + "We're happy to see you back on Sunduk!\n"
                     + "You have successfully logged in to your account.\n\n"
                     + "If this wasn't you, please "
                     +  "secure your account immediately.\n\n"
                     + "Team Sunduk";
        } else {
            return "Assalamualaikum " + event.getFullName() + ",\n\n"
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


    public String buildSubjectForOtp(final OtpEvent event) {
        return "Asslamualaikum " + event.getFullname() + ", Your Sunduk MPIN Reset OTP";
    }
    public String buildBodyForOtp(final OtpEvent event) {

        String html = """
                <!DOCTYPE html>
                        <html xmlns="http://www.w3.org/1999/xhtml">
                        <head>
                          <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                          <meta name="x-apple-disable-message-reformatting" />
                          <title>4 Digit PIN To Reset Sunduk MPIN</title>
                          <link rel="preconnect" href="https://fonts.googleapis.com">
                          <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                          <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
                         <style>
                            * {
                              font-family: "Poppins", sans-serif !important;
                              font-style: normal !important;
                            }
                            body, #bodyTable, #bodyCell {
                              height: 100%% !important;
                              margin: 0;
                              padding: 0;
                              width: 100%% !important;
                              background-color: #f4f4f4;
                            }
                            table { border-collapse: collapse; }
                            img { border: 0; outline: none; text-decoration: none; }
                            p { margin: 0; padding: 0; }
                            a { text-decoration: none; }
                
                            .container {
                              max-width: 600px;
                              width: 100%%;
                            }
                
                            .white-card {
                              background-color: #ffffff;
                              border-radius: 16px;
                              box-shadow: 0 2px 6px rgba(0,0,0,0.05);
                              overflow: hidden;
                            }
                
                            .content-cell {
                              color: #333333;
                            }
                
                             .logo-container {
                             margin-top: 25px;
                             margin-bottom: 25px;
                             display: flex;
                             justify-content: flex-start;
                             align-items: center;
                             gap: 10px; 
                            }
                
                            .logo-img {
                              height: 90px;
                              width: 90px;
                            }
                
                            .logo-text {
                              font-size: 22px;
                              color: #C19945;
                              font-weight: 700;
                            }
                
                            .logo-text {
                              font-size: 20px;
                              color: #C19945;
                              font-weight: 700;
                            }
                
                            .title-bar {
                              font-size: 20px;
                              color: #666666;
                              font-weight: 500;
                              text-align: center;
                              border-top: 1px solid #f0f0f0;
                              border-bottom: 1px solid #f0f0f0;
                              padding: 12px 0;
                            }
                
                            .inner-content {
                              padding: 28px;
                            }
                
                            .greeting {
                              font-size: 18px;
                              margin-bottom: 16px;
                            }
                
                            .greeting strong {
                              color: #000;
                            }
                
                            .greeting span {
                              color: #C19945;
                              font-weight: 600;
                            }
                
                            .text-muted {
                              font-size: 14px;
                              color: #666666;
                              line-height: 1.6;
                            }
                
                            .pin-label {
                              font-size: 16px;
                              color: #333333;
                              font-weight: 600;
                              margin-top: 25px;
                              margin-bottom: 8px;
                            }
                
                            .pin-box {
                              font-size: 22px;
                              color: #C19945;
                              font-weight: bold;
                              letter-spacing: 4px;
                              padding: 8px 0;
                              display: inline-block;
                            }
                
                            .pin-box span {
                              color: #333;
                              font-weight: 500;
                            }
                
                            .security-title {
                              font-size: 16px;
                              color: #333333;
                              font-weight: 600;
                              margin-top: 25px;
                              margin-bottom: 10px;
                            }
                
                            .security-list {
                              font-size: 13px;
                              color: #666666;
                              line-height: 1.8;
                              padding-left: 0;
                              margin-top: 0;
                              list-style: none;
                            }
                
                            .security-list li {
                              margin-bottom: 6px;
                            }
                
                            .security-list span {
                              color: #C19945;
                              font-weight: 600;
                            }
                
                            .help-title {
                              font-size: 15px;
                              color: #000;
                              font-weight: 600;
                              margin-top: 20px;
                              margin-bottom: 8px;
                            }
                
                            .help-text {
                              font-size: 13px;
                              color: #999999;
                              line-height: 1.6;
                              margin-bottom: 25px;
                              white-space: nowrap;
                            }
                
                            .help-text a {
                              color: #C19945;
                              font-weight: 500;
                              text-decoration: none;
                            }
                
                            .footer {
                              text-align: left;
                              font-size: 15px;
                              color: #999999;
                              line-height: 1.6;
                              font-weight: 500;
                              margin-top: 10px;
                              margin-bottom: 25px;
                            }
                
                            @media only screen and (max-width: 600px) {
                              .container { width: 100%% !important; max-width: 100%% !important; }
                              .content-cell { padding: 20px 15px !important; }
                              .inner-content { padding: 20px !important; }
                            }
                          </style>
                        </head>
                
                        <body>
                          <table border="0" cellpadding="0" cellspacing="0" width="100%%" id="bodyTable">
                            <tr>
                              <td align="center" valign="top" id="bodyCell">
                                <table border="0" cellpadding="0" cellspacing="0" width="600" class="container">
                                  <tr><td height="40"></td></tr>
                                  <tr>
                                    <td align="center">
                                      <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="white-card">
                                        <tr>
                                          <td class="content-cell">
                
                                            <div class="logo-container">
                                             <div style="display: flex; ">
                                             <div><img src="cid:logoImage" alt="Sunduk Pay Logo" class="logo-img" /></div>
                                             <div><span class="logo-text">Sunduk Pay</span></div>
                                             </div>
                                             
                                             
                                             
                                            </div>
                
                                            <div class="title-bar">4 Digit PIN To Reset Sunduk MPIN</div>
                
                                            <div class="inner-content">
                                              <div class="greeting">
                                                <strong>Hi</strong> <span>%s</span>,
                                              </div>
                
                                              <div class="text-muted">
                                                We received a request to <strong style="color:#333;">reset</strong> your PIN for your Sunduk Account.
                                              </div>
                
                                              <div class="text-muted" style="margin-top:10px;">
                                                To proceed, please use the <strong style="color:#333;">4-digit verification MPIN</strong> below to reset your MPIN securely in the Sunduk app.
                                              </div>
                
                                              <div class="pin-label">Your Verification PIN</div>
                                              <div class="pin-box"><span>[</span>%s<span>]</span></div>
                
                                              <div class="text-muted" style="margin-top:15px;">
                                                This PIN is valid for <strong style="color:#333;">Today</strong> and can only be used once.<br>
                                                Enter this code on the <strong style="color:#333;">“Verify OTP”</strong> screen in your Sunduk app to set your new MPIN.
                                              </div>
                
                                              <div class="text-muted" style="margin-top:15px; font-weight:600; color:#333;">
                                                If you did not request this reset, please ignore this message. Your account will remain secure.
                                              </div>
                
                                              <div class="security-title">Security Tips</div>
                
                                              <ul class="security-list">
                                                <li><span>#</span> Never share this PIN or your MPIN with anyone, including Sunduk representatives.</li>
                                                <li><span>#</span> Sunduk will never ask for your PIN via call, SMS, or email.</li>
                                                <li><span>#</span> Always use the official Sunduk app or website.</li>
                                              </ul>
                
                                              <div class="help-title">Need Help ?</div>
                
                                              <div class="help-text">
                                                Contact our support team at [<a href="mailto:support@sunduk.com">support@sunduk.com</a>]
                                              </div>
                
                                              <p class="footer">© 2025 Sunduk Technologies Pvt. Ltd.</p>
                                            </div>
                
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </body>
                        </html>
        """;
        return String.format(html, event.getFullname(), event.getOtp());
    }

}

