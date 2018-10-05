<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<section id="2fa-options">

    <div class="col-sm-12" style="width: 65%; margin-bottom: 20px;">
        <h1 style="font-size: 32px;">Two Factor Authentication (2FA) is <span style="color:red">Disabled</span> </h1>
        <!-- disable -->
        <div class="g2fa_connect" style="width: 45%; float:left;" hidden>
                <h4 style="margin-top: 0;">Two Factor Authentication Disabled</h4>
                <div>For extra account security, we strongly recommend you enable two-factor authentication (2FA).</div>
                <hr>
                <form action="#">
                    <div style="margin-bottom: 4px; ">
                        <span style="float:left;">Username/Email:</span>
                        <span style="float:right;">${user.getEmail()}</span>
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 12px; ">
                        <span style="float:left;">Password:</span>
                        <input id="2fa_user_pass" type="password" style="float:right; border: 1px solid grey;">
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 12px; ">
                        <span style="float:left;">Code:</span>
                        <input type="text" style="float:right; border: 1px solid grey;">
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 8px;"><b>Before turning on 2FA, write down or save a backup of your 16-digit key and put it in safe place.</b> If your phone
                        gets lost, stolen, or erased, you will need this key to get back into you account!</div>
                    <div style="margin-bottom: 24px; max-width: 230px;">
                        <input type="checkbox" name="" value="" style="vertical-align: middle;margin: 0;">
                        <span style="vertical-align: middle;">I have backed up my 16-digit key.</span>
                        <div class="clearfix"></div>
                    </div>
                    <button class="g2fa_connect_button btn btn-default" style="float:right;" disabled>Enable 2FA</button>
                </form>
        </div>
        <!-- enable -->
        <div class="g2fa_connected" style="width: 45%; float:left;" hidden>
              <h4 style="margin-top: 0;">Two Factor Authentication Enabled</h4>
              <div>If you want to turn off 2FA, input your account password and the 16-digit code provided by the Google Authenticator app below, then click "Disable 2FA.</div>
              <hr>
              <form action="#">
                <div style="margin-bottom: 4px; ">
                  <span style="float:left;">Username/Email:</span>
                  <span style="float:right;">example@example.com</span>
                  <div class="clearfix"></div>
                </div>
                <div style="margin-bottom: 12px; ">
                  <span style="float:left;">Password:</span>
                  <input type="text" style="float:right; border: 1px solid grey;">
                  <div class="clearfix"></div>
                </div>
                <div style="margin-bottom: 24px;">
                  <span style="float:left;">Code:</span>
                  <input type="text" style="float:right; border: 1px solid grey;">
                  <div class="clearfix"></div>
                </div>

                <button class="btn btn-default" style="float:right;">Disable 2FA</button>
              </form>
        </div> -->
        <!--  qr code block -->
        <div class="g2fa_connect" style="width: 43%; float:right;" hidden>
            <div id="qr" style="margin-bottom: 24px; height: 280px;">
                <%--<img style="width: 100%;height: 100%;" src="../Desktop/card.png" alt="">--%>
            </div>
            <div><b>16-Digit-Key:</b> <span id="g2fa_code" style="color:red; text-transform: uppercase;"></span></div>
            <div style="margin-bottom: 24px;">Save a backup of your recovery key</div>
            <div>
                NOTE: This code changes each time you enable 2FA.
                If you disable 2FA this code will no longer be valid.
            </div>
        </div>
        <div class="clearfix"></div>
    </div>



</section>