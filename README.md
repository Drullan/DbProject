# Database project LTH 2017-04-12
## Collaborators
Erik Andersson Data 2013 erik.andersson.811@student.lu.se
Shan Langlais Data 2013
## Introduction
We've been assigned to implement a database for the supervision of production and delivery of cookies produced by the company Krusty Kookies Sweden AB.
## Getting started
Add the file sqlite-jdbc.jar to the build path and run.
The database is connected with a relative path so it should work 
right away, however if you don't get connected you can set your direct
path to db.db in the file FactoryGUI on line 36. The db.db is located in
Project/
## Running the program
If views aren't updated instantly try swapping between the panes.
There should be two products listed as test data: sushi and cake. There
are 9 ingridients listed to begin with. You can add more if you want to.
There are 5 panes:
* Order pallet
* Create pallet
* Overview pallets
* Create reciepe
* Overview ingridients
### Order pallet
Here you can place an order on any product that exists in the database.
If you place an order on a product that currently have no pallets ready
the order will be pending and will get pallets added to it when they are
made.
### Create pallet
Here you can create pallets of products that exists in the database if
there are enough ingridients to do so. If there are any pending orders 
to the pallet you made it will be mapped to that order directly. Otherwise
it will be placed in the freezer and wait for new orders.
### Overview pallets
Here you can view all pallets that have ever been made and check their 
status. You can filter the table by entering values. It's possible to 
block pallets by either using the checkboxes in the view or by using the
block button. If the view isn't updated try switching between the views.
### Create reciepe
Here you can create a new product by adding ingridients to it. When entering
ingridients that aren't listed in the database an options box will ask you
if you want to add them to the database. 
### Overview ingridients
Here you can see which ingridients are currently listed in the database and
their current status. If you want to add ingidients to the quantity in storage
simply use a date before todays date. Switch between any of the panes to update
the database.  
## Review code
If you want to check out the code we would recommend Database.java since
that's the file that contains all relevant code for this course. The gui
classes are quite messy and uggly but feel free to check them out if you
are bored.
## Database overview
