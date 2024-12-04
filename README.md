Must have javafx v17 in the same file as this to be able to run it. If you don't it won't run properly.

Make sure that you put these guys into a folder.

For your launch.json, make sure that you change what's given to you here:

{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Current File",
      "request": "launch",
      "mainClass": "${file}"
    },
    {
      "type": "java",
      "name": "Jordle",
      "request": "launch",
      "mainClass": "Jordle",
      "projectName": "JordleFx_d56a5b93",
      "vmArgs": "--module-path <THIS SHOULD BE YOUR RELATIVE TO THE FOLDER WHERE YOUR JAVAFX IS LOCATED> --add-modules javafx.controls,javafx.media,javafx.graphics,javafx.fxml"
    }
  ]
}

Create the project and upload the files to your referenced libraries and it should give you a settings.json by default. Then you should be able to run it.
