/** Mobile Redirect Helper
 *
 *  Redirects to a wikimedia-mobile installation for viewers on iPhone, iPod 
 *  Touch, Palm Pre, and Android devices.
 *
 *  You can turn off the redirect by setting the cookie "stopMobileRedirect=true"
 */
if ( /(Android|iPhone|iPod|webOS|NetFront|Opera Mini|SEMC-Browser|PlayStation Portable|Nintendo Wii|BlackBerry)/
	.test( navigator.userAgent ) )
{
  
  if (    (document.cookie.indexOf("eRedirect=t") < 0)  // Don't redirect if we have the stop cookie ... only testing a subportion of the cookie. Should be REALLY unique!
       && (wgNamespaceNumber >= 0)                 // Don't redirect special pages
       && (wgAction == "view"))                    // Don't redirect URLs that aren't simple page views 
  {
    // If we've made it here, then we are going ahead with the redirect
    var url = wgWikimediaMobileUrl;
    // If we are NOT on the main page, then set the pageName!
    if (wgPageName != wgMainPageTitle.replace(/ /g, '_')) {
      url += '/' + encodeURI(wgPageName);
    }

    document.location = url;
  }
}
