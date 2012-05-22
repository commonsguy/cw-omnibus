Welcome to the source code for _The Busy Coder's Guide to Android
Development_!

All of the source code in this archive is licensed under the
Apache 2.0 license except as noted.

The names of the top-level directories roughly correspond to a
shortened form of the chapter titles. Since chapter numbers
change with every release, and since some samples are used by
multiple chapters, I am loathe to put chapter numbers in the
actual directory names.

## Using in Eclipse

These projects can be imported using the normal Eclipse import process.

Note, though, that you will have to fix some things up, particularly if you
are getting errors:

- The build target of the project may be an Android SDK that you do not have
installed. You will need to set the project build target to something that
you have, by means of Project Properties.

- Many of these projects use [ActionBarSherlock](http://actionbarsherlock.com).
There is a copy of a compatible ActionBarSherlock in `external/`, and the 
project files are set up to reference that copy. If you import it first, your
imports of other sample apps should go more smoothly. Alternatively, you 
can download and set up ActionBarSherlock yourself in your Eclipse workspace,
then go into Project Properties and point the
book's project to use your copy of the ActionBarSherlock library project.

- Many of the book samples, and ActionBarSherlock, require your Java compiler
compliance level to be set to 1.6. You can find this in Project Properties,
in the Java Compiler area.

## Using from Ant

If you wish to use this code, you should delete build.xml from the project, then run
  `android update project -p ...`  (where ... is the path to a project of interest)
	on those projects you wish to use, so the build files are
	updated for your Android SDK version.

