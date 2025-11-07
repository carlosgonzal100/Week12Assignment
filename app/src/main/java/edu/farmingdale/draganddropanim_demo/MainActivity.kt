package edu.farmingdale.draganddropanim_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.farmingdale.draganddropanim_demo.ui.theme.DragAndDropAnim_DemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragAndDropAnim_DemoTheme {

                    DragAndDropBoxes()


            }
        }
    }
}



// This should be completed in a group setting
// ToDo 1: Analyze the requirements for Individual Project 3
/**
 * Todo1: well i ran the program and everything seems off, i see there is a
 * person off the screen and anything i thouch isnt interactable. this project
 * needs a way to click and drag items on the screen. This project also need
 * a recentering or reworking of the screen in order to see all items. The screen
 * also seems to be locked into the landscape mode. Now i dont know if this is
 * how its suppossed to be but i will look into it.
 */

// ToDo 2: Show the DragAndDropBoxes composable
/** this i was confused on a bit. it sais show thw the DragAndDropBoxes composable
 * the thing would be that when i cloned the project was already on the screen. and
 * even in the set content, the composable is already set. so this Todo 2 is done automatically
 */

// ToDo 3: Change the circle to a rect
/** finished it, did exactly what was asked*/

// ToDo 4: Replace the command right with a image or icon
/** did what was asked, done*/

// ToDo 5: Make this works in landscape mode only
/** we can see that the screen orientation in the app manifest is
 * already set onto landscape mode. this means that the orientation
 * is locked to landscape mode
 */

// ToDo 6: Rotate the rect around itself
/**i was a bit confused. i did exactly what i was asked.
 * i implemented code using the android studios website
 * that causes the rectangle to rotate from its center
 * no matter what. when i move the icon it moves up and rotates
 * but then it moves down, rotates for a while, then stops.
 * i made it rotate on its center so there you go
 */

// ToDo 7: Move - translate the rect horizontally and vertically
/**
 * rectangle moves up down left and right, i even added arrows
 * so the user can tell what direction each box represents
 */

// ToDo 8: Add a button to reset the rect to the center of the screen
/**Button that resest to the center of the screen is good*/

// ToDo 9: Enable certain animation based on the drop event (like up or down)


// ToDo 10: Make sure to commit for each one of the above and submit this individually


