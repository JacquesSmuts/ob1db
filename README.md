# ob1db

This is my submission for a simple film-viewer app, as part of my interview process.

The specifications are available in a pdf in the root, called Star Wars App - Interview Pack. There are some screenshots below, as well as answers to questions in the pdf.

## Screenshots
### Splash Screen
![star wars 1](https://user-images.githubusercontent.com/8026691/35240700-f5506cb6-ffbc-11e7-9f68-09c1738cc3ac.PNG)

### List Screen
![star wars 2](https://user-images.githubusercontent.com/8026691/35240701-f57c7c16-ffbc-11e7-9666-5e6677615a64.PNG)

### Film Detail Screen
![star wars 3](https://user-images.githubusercontent.com/8026691/35240702-f5a9cf68-ffbc-11e7-9f50-7d74a50a5ea3.PNG)

## Questions
### Api Testing
1 + 2 - I have never done load testing or a test-suite for a REST testing tool. I can learn to do that, but I had limited time and left this for last.

### Mobile App Testing
1. Functional Test Cases

I have created a simple Espresso Test which opens the app and clicks on the first item in the listview, and checks that the detail screen opens. If I have time I will also add checks for appropriate user data on this test. There is also a function to disable the internet and create a similar Espresso test for if there is no internet.

This testing strategy can ensure that there is always data and there are no crashes under normal circumstances. In order to ensure that the app performs under other circumstances like OS's, screen sizes and devices, would require a more comprehensive testing strategy, but this provides a good starting point.

Things like UI, UX and readability are best testing using a hallway test with a technologically illiterate friend or relative.

Finally, the specifications drawn up as part of the agreement with the client needs to be manually tested against the specifications before delivery, preferably by someone who was part of the client negotiations, and finally side-by-side with the client to ensure contractual delivery.

2. Interruption Test Cases

The previously mentioned Espresso test can be expanded upon with a simple test to add screen rotation using the method described [here](https://stackoverflow.com/questions/37362200/how-to-rotate-activity-i-mean-screen-orientation-change-using-espresso). Screen orientation is used because it follows a very similar Activity Lifecycle to interruption, except that the available memory for the savedInstanceState Bundle decreases over time for any background Activity, which is why I don't save objects in the savedInstanceState, but only ID's, so the database Loader can obtain the item after Activity Restart.

Handling navigating away is a bit more difficult and likely requires the [UI Automator](https://developer.android.com/training/testing/ui-testing/uiautomator-testing.html) which I have no experienc with.

The quickest way to handle that is to have a simple "Navigate away from every screen" manual testing procedure before each deployment, but this becomes exponentially impractical as the number of screens increases.

3. Performance Test Cases

As per previous answer, logging can be expanded upon and logs recorded using an analytics API such as Firebase to determine the average length of any process. This method will also allow you to see which circumstances (user location, connectivity, hardware, os) compromises the app's performance the most.

4. UI Testing

Aside from answers already given above to test screen orientation, things like font options need to be tested manually for each new TextView or EditText within the app.
