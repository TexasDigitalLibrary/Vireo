
How To Upgrade Bootstrap for Vireo
==================================

1. Check that no one has modified `public/boostrap/*.`. If someone has then go flog 
   them.... and do it publicly. Then clean up their mess.

2. Go to Twitter's site and download both the source version of Bootstrap (we'll call 
   this `bootstrap-source`), and the compiled ready to go version (we'll call this 
   `boostrap-compiled`).

3. Clear out the old version of bootstrap:

  rm -rf public/bootstrap/css
  rm -rf public/bootstrap/img
  rm -rf public/bootstrap/js
  rm -rf public/bootstrap/less
  # And anything else that is not this file.
  
4. Copy everything over from the `bootstrap-compiled`. This will give you the css, img, and js directories.

  cp -R bootstrap-compiled/css   public/bootstrap/css
  cp -R bootstrap-compiled/img   public/bootstrap/img
  cp -R bootstrap-compiled/js    public/bootstrap/js

5. Next grab the `less` directory from `bootstrap-source`, but leave the test's directory.

  cp -R bootstrap-source/less   public/bootstrap/less
  rm -rf public/bootstrap/less/tests