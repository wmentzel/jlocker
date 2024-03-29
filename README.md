# jLocker

a Java locker manager

![](screenshots/main-frame.png "jLocker main view")

## Introduction

I wrote this Java Swing application for my former school at which I made my Abitur. I started working on it in 2008 and
the first version was released 2009. This application is as far as I know used to this day.

I had to change the name from “jLock” to “jLocker” because there already was a product with that name.

## Features

### User Management

By default two users can log-in, one with superuser privileges and one with limited privileges (cannot see lock codes)

### Optimization

If a (school) class is moved to another room, jLocker can automatically move the pupil’s lockers as well by finding
lockers with the shortest distance to the new class room also considering pupils heights (some lockers are to far up for
5th graders to reach)

### Security

All data is DES encrypted with a password of a length of at least 8 digits.

### Ease of Use

jLocker uses a hierarchy of buildings, floors, walks and managment units (which are either a room or a staircase) which
makes it possible to represent any layout a school or university may have. All this can be created easily at run time.

### Data Is Yours

jLocker works completely offline and only saves data locally on your PC.

### Platform Independent:

jLocker can run on any operating system (any version of Windows, Mac OS or Linux) for which
there is a Java Virtual
Machine

### Search

You can search for any criteria of a locker within jLocker (pupils name, height…).

### Free and Open Source

jLocker is open source and can be used by anyone for free

### Backups

You can specify the number of data states that should be preserved to recover data that was deleted
unintentionally.

## How to build JAR file

Run the maven "package" task