# BCS25-Project-1-2: Phase 1 DISTANCE AND TIME CALCULATORS

Group 25 Team Members: Tristan Dormans, Mehmet Levent Koca, Alen Quiroz Engel, Vlad Creciun, Xuhan Zhuang, Joel Kumi, Bruno Torrijo

## IMPORTANT TO BE ABLE TO RUN THE APPLICATION
Set up a JavaFX environment on your IDE to be able to run the application GUI.

You might also need to set up the VM options in your run configurations as: 
--module-path "your physical path to JavaFX lib" --add-modules javafx.controls,javafx.fxml
     


***** HOW TO RUN *****

Launch the application by running the MapLauncher.java file. This will start up the aplication, a terminal will pop up that starts
the local graphhopper server, then the GUI will pop up on screen.

If the terminal does not pop up, please run the following command from the source folder to start the local graphhopper server: 
java -Xms1g -Xmx1g -server -Ddw.graphhopper.datareader.file=src/java/graphhopper/Maastricht.osm.pbf -cp src/java/graphhopper/graphhopper.jar com.graphhopper.application.GraphHopperApplication server src\\java\\graphhopper\\config.yml

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

4) 'Toggle graphhopper' Button
    Toggle to enable the calculation of the actual distance by car, press the search button again to refresh the screen after toggling.

4) Information on Screen
    After the 'Search' button is clicked, the user is presented with different pieces of information on screen:
        - There is a clear red line shows between the two points, giving a visual representation of the 'straight line distance' between 
        the two points.
        - To the very right of the GUI, some text is also displayed, giving the user information on the exact distance in kilometers 
        or meters, the 'Average time by walk', 'Average time by bike', and 'Average time by car'. All of these time markers are calculated 
        based on average speed of the different traveling times and the straight line distance calculated.

5) To exit the program simply press the cross on the top right of the GUI, or alternatively press 'ALT' + 'F4'. Repeat this step for the terminal window.

Thank you,
Group 25.

21st of March, 2024.
