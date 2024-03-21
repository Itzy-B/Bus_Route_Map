# BCS25-Project-1-2: Phase 1 DISTANCE AND TIME CALCULATORS

Group 25 Team Members: Tristan Dormans, Mehmet Levent Koca, Alen Quiroz Engel, Vlad Creciun, Xuhan Zhuang, Joel Kumi, Bruno Torrijo

## IMPORTANT TO BE ABLE TO RUN THE APPLICATION
Set up a JavaFX environment on your IDE to be able to run the application GUI.

You might also need to set up the VM options in your run configurations as: 
--module-path "your physical path to JavaFX lib" --add-modules javafx.controls,javafx.fxml
     


***** HOW TO RUN *****

Launch the application by running the MapLauncher.java file. This will start up the aplication and the GUI will pop up on screen.

Now, there are multiple things you can do:
1) The '+' and '-' buttons
    These buttons are used for zooming the map in and out, if the user desires to have a closer or farther away view of the map, 
    they will be able to control the zoom in amount with these buttons

2) Zip Code text fields
    There are two text fields labeled "Enter Zip Code 1" and "Enter Zip Code 2", these are for the user to insert their desired points 
    in the map. By inserting 2 Zip Codes, the user will be able to clculate the distance between the two points in the map.

3) 'Search' Button
    Once the two desired Zip Codes have been inserted, the user is able to press the 'Search' button. This runs the algorithm to 
    calculate the distance betwen the two points in the map and shows a visual represenation of the distance between the two points.

4) Information on Screen
    After the 'Search' button is clicked, the user is presented with different pieces of information on screen:
        - There is a clear red line shows between the two points, giving a visual representation of the 'staright line distance' between 
        the two points.
        - To the very right of the GUI, some text is also displayed, giving the user information on the exact distance in Kilometers 
        or Meters, the 'Average time by walk', 'Average time by bike', and 'Average time by car'. All of these time markers are calculated 
        based on average speed of the different traveling times and the straight line distance calculated.

5) To exit the game simply press the cross on the top right of the GUI, or alternatively press 'ALT' + 'F4'

Thank you,
Group 25.

21st of March, 2024.


## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
