# awtswing
Simple graphic examples with AWT & Swing in Java. I started experimenting with AWT and Swing for drawing graphs in my other project [BibleLoops](https://github.com/gregdott/bibleloops). There were a few things that I had to figure out in conjunction to get things working the way that I wanted. This repo is a distillation of what I learned from that process.

## Notes:

- There is an `@override` of the `getPreferredSize()` method in `GraphicsPanel.java`.
That, in conjunction with 
```frame.setContentPane(gp); 
frame.pack();
frame.setMinimumSize(frame.getSize())```
is used to ensure that the frame gets created with the desired width and height. If we only use `frame.setSize(width, height)` it is possible that the created window will not be exactly those dimensions. There is info in the javadoc about why this is the case.


## What can it do?

Right now this project just contains some basic examples of drawing and moving shapes using AWT and Swing in Java. I'll add a more detailed description here at some point. This aims to be a nice resource for easily understanding how to draw, move and manipulate shapes without having to scour Stackoverlow for 10 different things. This is still a work in progress.

## Side Note:

This was made as a Maven project in case I expand it further and need to include some or another dependency. For now though (as of 12-11-2022) there are no dependencies, so it does not need to be used as a Maven project. You can find the relevant files in [awtswing/src/main/java/](https://github.com/gregdott/awtswing/tree/main/awtswing/src/main/java/com/awtswing). The most important file at present is GraphicsPanel.java. That's where all the meat is for now.
