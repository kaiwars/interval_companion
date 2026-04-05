# Purpose

This app shall acousticly accompany my interval training.

# General Layout
On the top left there is the main menu (hamburger).
It has the menu items
- Go
- Settings
as described below

# Go

This is the main screen. It is opened at application start. 

It has a big play button and a pause button.

Below that the user sees the current status in two columns with a header on top and some information below:
- "Round" with a large number
- "Interval" with the name of the interval name that is being executed

below that is a running time in minutes and seconds (MM:SS). If the time exceeds an hour, it is displayed as HH:MM:SS

When the user taps play, time starts running. The play button turns into a stop button

The defined and active (checked) rounds are executed round robin indefinitely until the user taps stop.
The round number displayed starts with 1 and counts up for each round executed.

For each round the intervals are executed in order (only intervals with duration > 0 )

According to the settings interval names change after the alloted time.

Whenever a round or interval starts or ends the respective sound is played (if checked, at the point in time configured).

The pause button pauses and resumes execution. 

the stop button stops and resets.

when there are no rounds configured, there is a red text at the bottom:
"Define rounds in the settings first!". Tapping on this text leads the user to the round settings screen. 


# Help

This drawer menu opens a help screen explaining how the app works. 
on each screen (except the help screen itself) in the top bar on the right edge there is a blue circle with a white question mark inside. Tapping it also leads to the help screen.



# Settings

The main menu contains a menu item "Settings".
This opens a submenu of diffent settings according to the next subchapters. Each of these submenus opens a specific settings screen.
All settings are saved in app specific storage.

there is a back button top left 
- when we are in one of the settings detail screens this button returns to the settings main menu
- when we are in the settings main menu, it returns to the "go" screen.


## Rounds

There is an explanatory text at the top:
"Each round consists of one to three intervals with a duration in seconds. Zero indicates this interval is omitted. Checked rounds are executed in order, unchecked rounds are omitted."

after this text is a list of rounds, initially empty. 
Each round is one line, consisting of:
- a check mark
- three integer input fields (empty = interval skipped; values ≤ 0 are not allowed; at least one field must be non-empty)
- a trash icon

Below the list is a green plus button to add a new round.


## Interval Names

There is an explanatory text at the top:
"Each round consists of one to three intervals, named e.g. "fast, slow, chill".
below that are three text input fields, preceded by the labels:
- "1st Interval"
- "2nd Interval"
- "3rd Interval"
 
 they are prefilled with "fast", "slow" and "chill"
 

## Voice Playback

There is an explanatory text at the top:
"For each Interval the interval name is played (if recorded), either at the beginning or the end of the interval.
For each round the round number is played (if recorded), either at the beginning of the round (before the first interval name) or at the end of the round, after the last interval name."

There are two groups of radio buttons:

Interval name
- play before
- play after
- don't play

Round number
- play before (before the first interval name announcement of the round)
- play after (after the last interval name announcement of the round)
- don't play


## Voice Recording

there are several items, that allow the user to record a short audio clip.
each item consists of
- a record/stop button
- a playback button
- a text explaining what this audio is for

the following items are displayed (texts) in two groups:

Interval names
- first 
- second 
- third 
(when the user entered his own interval names, these are used instead of first, second, third)


Round numbers
- 01
- 02
- 03
- 04
- 05
- 06
- 07
- 08
- 09
- 10

and there is a green plus button which adds another number.


when the user taps one of the record buttons, audio recording starts and the record button turns into a stop button. when the user taps the stop button the recorded audio is saved.

when no audio has been recorded, the corresponding checkbox is disabled. after it is recorded, the checkbox is enabled. 


## Audio Focus

There is an explanatory text at the top:
"This setting defines how the audio clip is played over the background music when an interval starts or ends."

There are the following, mutually exclusive radio buttons:
- Duck (lower music volume)
- Pause and resume

By default Duck is selected.






# Technology

Android