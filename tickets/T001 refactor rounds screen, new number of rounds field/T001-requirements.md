I want to rework the rounds screen. It needs a fourth input field and up/down reorder buttons, which would be too much with the current design.

It has two modes, regular and edit. It starts in regular mode. In the top bar, to the left of the help icon is a segmented control with two icons [ ✏️ | ☰ ] (a pencil and an icon symbolizing rows) that switches between edit mode and regular mode.  The switch impacts all rounds displayed.

In edit mode:
- A row/round has the three interval input fields as before.
- A fourth number field to the right of these three. It tells how many times this round is repeated before going to the next round. In the execution loop this behaves as if there were that many identical consecutive rows.
- all 4 fields allow inputs 0-999
- No checkbox and no trash icon.
- No validation: an all-empty round (all four fields blank) is saved as-is and simply does nothing during execution.

In regular mode a row has the following elements in this sequence:
- An up arrow button and a down arrow button to reorder the row.
- A checkbox to enable/disable the round. When unchecked, all four numbers are greyed out and italic.
- The four numbers as one label in the form 1/2/3-4, where empty interval slots keep their slash (e.g. 30//90-2 when interval2 is empty).
- A trash icon to delete the round.

The plus icon to add a row is visible in both modes. It adds a new row, switches to edit mode, scrolls to the new row, and focuses its first input field.

The rounds list uses imePadding() so it scrolls above the soft keyboard when fields are being edited.