# TestTwitterApp
This app lets the user log in to any Twitter account, uses android speech recognition by Google to recognize what the user tells and tweets it.

This app utilizes two APIs -- Twitter REST API-- and --Google Speech Recognition API--. 

HOW IT WORKS:
1.-The app either recognizes a previously authenticated Twitter and goes straight to the main activity 
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/master/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM%20(3).jpeg?raw=true "Optional Title")

OR

authenticates the user according to a previously created Twitter account.
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/master/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM%20(4).jpeg?raw=true "Optional Title")


2.-Displays the a "Hello"+username message along with a "LOG OUT" button and a "Record" button.
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/master/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM%20(3).jpeg?raw=true "Optional Title")

3.-When the user selects the record button, the app utilizes the Google Speech Recognition Intent to recognize what the user talked about.
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/Final/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM.jpeg?raw=true "Optional Title")

3.- The app triggerrs an Activity that lets the user modify their message before tweeting, in case the Speech Recognition did not caught something right.
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/Final/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM%20(2).jpeg?raw=true "Optional Title")

4.-The app utilizes the Twitter REST API to tweet it into the desired account once the user clicks on the tweet button.
![Alt text](https://github.com/kevinjmz/TestTwitterApp/blob/Final/WhatsApp%20Image%202017-11-30%20at%209.57.16%20AM%20(1).jpeg?raw=true "Optional Title")


