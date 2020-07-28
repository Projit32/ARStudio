# Setting up your Dev Channel

**You'll need a hosting service. For that you can use [XAMPP](https://www.apachefriends.org/index.html) or [000Webhost](https://www.000webhost.com) as the FREE options.**

Alternatively, you can use services like AWS, Hostinger or GoDaddy, but they are all paid and not so easy to setup. I recommend **000webhost or XAMPP** for this purpose.

## Setup Instructions: 

* Setup your hosting platform.
  - If you're using **XAMPP**, simply install the applicaation and grant all firewall permission, if prompted, for public and private network, you'll need it.
  - if you're using **000webhost**, sign up for a free account and you'll be assigned website. Skip the tutorial by presing `It's not my first rodeo, take me to the cPanel`. 
    Register a name & a password for your website.
* Download both the scripts which is inside the `APIs` folder. To download the script, just open the script and  click the `RAW` button on top right.
  - **XAMPP** : wherever you have installed xampp, there will be a folder inside the xampp folder named as `htdocs`, i.e `xampp/htdocs`, create a folder inside htdocs
  named as **ARSR** and cut-paste both the scripts in it. Lastly, create two folders named as **objects** and **AnimatedObjects** inside the `ARSR` folder.
  
  Your folder structure should look something like this:
  ``` 
  +-- Xampp
  |     +-- htdocs (find this folder & go inside)
  |     |     +-- ARSR (Create this as new folder and go inside)
  |     |      |    +-- AnimatedObjects (Create this as new folder)
  |     |      |    +-- objects (Create this as new folder)
  |     |      |    +-- AnimatedObjects.php (copy and paste this here after downloading)
  |     |      |    +-- objects.php (copy and paste this here after downloading)
  ```
  
  - **000webhost** : Rightnow, it will show you 3 options. Choose `upload your site`. Incase you have logged out or closed the tab, just log in again, your webite's name
  will appear in a card, click `Manage website` then click `Upload Files`.
  Inside the `public_html` folder, add a folder namd as **ARSR**, inside which you'll
  need to create two more folders named as **objects** and **AnimatedObjects**. Now, in that same folder, that is the **ARSR** folder, upload the scripts that you have
  downloaded earlier.
  
  Your folder structure should look something like this:
  ``` 
  +-- /
  |     +-- tmp
  |     +-- public_html (Go inside this folder)
  |     |     +-- ARSR (Create this as new folder and go inside)
  |     |      |    +-- AnimatedObjects (Create this as new folder)
  |     |      |    +-- objects (Create this as new folder)
  |     |      |    +-- AnimatedObjects.php (drag and drop these from your computer)
  |     |      |    +-- objects.php (drag and drop these from your computer)
  ```
* Finally, the fun part. you can browse through sites like [Google Poly](https://poly.google.com/), [SketchFab](https://sketchfab.com/) or [CGTrader](https://www.cgtrader.com/), to find 3D models that suits your needs. Just be sure that you download it in
`GLTF` format.
  - Make sure that you don't download heavy models which is above 70-100 MB in size.
  - Not all the models downloaded from Sketchfab and CGTraders will work. So, you have to experiment. 
  - Whenever you download something in GLTF format, there are high chances that the `.bin` files and the texture folder will be all uncompiled in a zip file. 
  Use the [GLB Packer](https://glbpacker.glitch.me/) to pack everything in a single `.glb` file. 
  Just extract and drag everything and drop everything in the website. the GLB will automaticlly donwload as `out.glb`.
  - Rename the file to your wish and then copy-paste or upload it to their designated folders. 
  If the model is animated, then put or upload it in the `AnimatedObject` folder, else the `objects` folder.
* In the app, tap the Dev-Channel button(the 4th teal colored button on the left) and paste the URL in it. Finally, then press the + button to see the list of your models. 
  - **XAMPP** : URL will be `127.0.0.1` by default.
  - **000webhost** : URL will be displayed on a card when you log into the [cPanel](https://www.000webhost.com/members/website/list) of this hosting service.
  by default, your URL should be `https://YOUR_WEBSITE_NAME.000webhostapp.com/`.
