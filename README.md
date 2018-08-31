# Cordova Security Plugin

Check your certificates
Do pinning certification.

## Using

Create a new Cordova Project

    $ cordova create hello com.example.helloapp Hello
    
Install the plugin

    $ cd hello
    $ cordova plugin add https://github.com/merighifacundo/cordova-plugin-certificate-ssl.git
    
## Preference for pin certification


```
    <preference name="pinDomainName" value="google.com" />
    <preference name="pinTestUrl" value="https://google.com/?" />
    <preference name="pinOne" value="sha256/ABCDEF=" />
    <preference name="pinTwo" value="sha256/ABCDEF=" />
    <preference name="pinThree" value="sha256/ABCDEF=" />
```

## Preference for full certification

```
    <preference name="certificateName" value="google_com" />
    <preference name="certificateUrl" value="https://google.com" />
    
```

## Where to place the certificate:

platforms/android/res/raw



