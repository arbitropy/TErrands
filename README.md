# TErrands

**TErrands** is a project management app written in Java and designed using Javafx, it was created as a university project for the OOP course. 

## Table of Contents
- [Features](#features)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Disclaimer](#disclaimer)

## Features:
1. MySQL DB support for credential management.
2. Add collaborators to your project by email of other users. Project Creator will have admin control over the project.
3. Minimalistic UI. 
4. Currently the projects are being saved through serialization of objects.

## Usage
The app was not packaged or deployed, feel free to convert this to a maven or gradle project to deploy properly.
Follow these steps to run without packaging:
1. Clone the repo.
2. Open in VSCode.
3. Install recommended Java extensions.
4. Update launch.json config, change `--module-path` of the `vmArgs` to the lib folder.
5. Update `projectStorage` variable path from `TErrands/Main.java` file to the `src/Project Files` folder.
6. Launch.

## Screenshots
![Screenshot_60](https://github.com/arbitropy/terrands/assets/49722623/b7fe20a7-ffcb-4896-a7f1-d82109878921)
![Screenshot_59](https://github.com/arbitropy/terrands/assets/49722623/6914b886-c96e-4fe6-b203-19d77f8dc93f)
![Screenshot_61](https://github.com/arbitropy/terrands/assets/49722623/8985ff9c-00e5-4a1c-bf15-f16b9d39b598)
![Screenshot_62](https://github.com/arbitropy/terrands/assets/49722623/8fcd821b-2cd0-4d36-9a80-1b062e61ef9e)


## Disclaimer
Currently the software is just a prototype with few main features unimplemented, and some bugs to be fixed. You can check the progress here: 
https://trello.com/b/maTKnLWJ/terrands-javafx-project-22
