function org_placebooks_PlaceBookEditor(){
  var $intern_14 = '', $intern_11 = ' top: -1000px;', $intern_37 = '" for "gwt:onLoadErrorFn"', $intern_35 = '" for "gwt:onPropertyErrorFn"', $intern_20 = '");', $intern_38 = '#', $intern_48 = '&', $intern_91 = '.cache.js', $intern_40 = '/', $intern_46 = '//', $intern_75 = '04A5931D172F7FD82D741F49C0AFCE91', $intern_79 = '1F381D30DCE036555F2745ED626E16C9', $intern_80 = '23E9B5D76E5BF1811F3E8EF81AF339F9', $intern_81 = '30CCF536FDC1F8CE9319885C4DDBCDAF', $intern_82 = '4FAC72116F57D7A7792223AD7CBDFF07', $intern_83 = '531E258E76ECEEA029678872B58ABF81', $intern_84 = '773A1D19272FF18BA9A0278A4A03E1D2', $intern_90 = ':', $intern_76 = ':1', $intern_77 = ':2', $intern_29 = '::', $intern_100 = ':moduleBase', $intern_13 = '<!doctype html>', $intern_15 = '<html><head><\/head><body><\/body><\/html>', $intern_32 = '=', $intern_39 = '?', $intern_85 = 'A383D93DB0FFE4B237D704EFB63E4E23', $intern_86 = 'B6AA4C17444A3B4089C196C1238A3CEE', $intern_34 = 'Bad handler "', $intern_12 = 'CSS1Compat', $intern_18 = 'Chrome', $intern_87 = 'D29CEAA98C8CEDAB9E7739ADAEFF642D', $intern_17 = 'DOMContentLoaded', $intern_6 = 'DUMMY', $intern_88 = 'F418E4F43979909BDA218D9B8ED5CD9D', $intern_89 = 'F9B6BEEB330DCF649386A8D45DCE6E2E', $intern_61 = 'Unexpected exception in locale detection, using default: ', $intern_60 = '_', $intern_99 = '__gwtDevModeHook:org.placebooks.PlaceBookEditor', $intern_59 = '__gwt_Locale', $intern_54 = 'android', $intern_45 = 'base', $intern_43 = 'baseUrl', $intern_1 = 'begin', $intern_7 = 'body', $intern_0 = 'bootstrap', $intern_42 = 'clear.cache.gif', $intern_31 = 'content', $intern_78 = 'cy', $intern_55 = 'desktop', $intern_57 = 'en', $intern_98 = 'end', $intern_19 = 'eval("', $intern_47 = 'formfactor', $intern_70 = 'gecko', $intern_71 = 'gecko1_8', $intern_2 = 'gwt.codesvr.org.placebooks.PlaceBookEditor=', $intern_3 = 'gwt.codesvr=', $intern_36 = 'gwt:onLoadErrorFn', $intern_33 = 'gwt:onPropertyErrorFn', $intern_30 = 'gwt:property', $intern_25 = 'head', $intern_95 = 'href', $intern_97 = 'http://fonts.googleapis.com/css?family=Mako&v2', $intern_69 = 'ie6', $intern_68 = 'ie8', $intern_67 = 'ie9', $intern_8 = 'iframe', $intern_41 = 'img', $intern_52 = 'ipad', $intern_49 = 'iphone', $intern_50 = 'ipod', $intern_22 = 'javascript', $intern_9 = 'javascript:""', $intern_92 = 'link', $intern_96 = 'loadExternalRefs', $intern_56 = 'locale', $intern_58 = 'locale=', $intern_26 = 'meta', $intern_51 = 'mobile', $intern_24 = 'moduleRequested', $intern_23 = 'moduleStartup', $intern_66 = 'msie', $intern_27 = 'name', $intern_63 = 'opera', $intern_4 = 'org.placebooks.PlaceBookEditor', $intern_74 = 'org.placebooks.PlaceBookEditor.devmode.js', $intern_44 = 'org.placebooks.PlaceBookEditor.nocache.js', $intern_28 = 'org.placebooks.PlaceBookEditor::', $intern_10 = 'position:absolute; width:0; height:0; border:none; left: -1000px;', $intern_93 = 'rel', $intern_65 = 'safari', $intern_21 = 'script', $intern_73 = 'selectingPermutation', $intern_5 = 'startup', $intern_94 = 'stylesheet', $intern_53 = 'tablet', $intern_16 = 'undefined', $intern_72 = 'unknown', $intern_62 = 'user.agent', $intern_64 = 'webkit';
  var $wnd = window;
  var $doc = document;
  sendStats($intern_0, $intern_1);
  function isHostedMode(){
    var query = $wnd.location.search;
    return query.indexOf($intern_2) != -1 || query.indexOf($intern_3) != -1;
  }

  function sendStats(evtGroupString, typeString){
    if ($wnd.__gwtStatsEvent) {
      $wnd.__gwtStatsEvent({moduleName:$intern_4, sessionId:$wnd.__gwtStatsSessionId, subSystem:$intern_5, evtGroup:evtGroupString, millis:(new Date).getTime(), type:typeString});
    }
  }

  org_placebooks_PlaceBookEditor.__sendStats = sendStats;
  org_placebooks_PlaceBookEditor.__moduleName = $intern_4;
  org_placebooks_PlaceBookEditor.__errFn = null;
  org_placebooks_PlaceBookEditor.__moduleBase = $intern_6;
  org_placebooks_PlaceBookEditor.__softPermutationId = 0;
  org_placebooks_PlaceBookEditor.__computePropValue = null;
  org_placebooks_PlaceBookEditor.__getPropMap = null;
  org_placebooks_PlaceBookEditor.__gwtInstallCode = function(){
  }
  ;
  org_placebooks_PlaceBookEditor.__gwtStartLoadingFragment = function(){
    return null;
  }
  ;
  var __gwt_isKnownPropertyValue = function(){
    return false;
  }
  ;
  var __gwt_getMetaProperty = function(){
    return null;
  }
  ;
  __propertyErrorFunction = null;
  var activeModules = $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || {};
  activeModules[$intern_4] = {moduleName:$intern_4};
  var frameDoc;
  function getInstallLocationDoc(){
    setupInstallLocation();
    return frameDoc;
  }

  function getInstallLocation(){
    setupInstallLocation();
    return frameDoc.getElementsByTagName($intern_7)[0];
  }

  function setupInstallLocation(){
    if (frameDoc) {
      return;
    }
    var scriptFrame = $doc.createElement($intern_8);
    scriptFrame.src = $intern_9;
    scriptFrame.id = $intern_4;
    scriptFrame.style.cssText = $intern_10 + $intern_11;
    scriptFrame.tabIndex = -1;
    $doc.body.appendChild(scriptFrame);
    frameDoc = scriptFrame.contentDocument;
    if (!frameDoc) {
      frameDoc = scriptFrame.contentWindow.document;
    }
    frameDoc.open();
    var doctype = document.compatMode == $intern_12?$intern_13:$intern_14;
    frameDoc.write(doctype + $intern_15);
    frameDoc.close();
  }

  function installScript(filename){
    function setupWaitForBodyLoad(callback){
      function isBodyLoaded(){
        if (typeof $doc.readyState == $intern_16) {
          return typeof $doc.body != $intern_16 && $doc.body != null;
        }
        return /loaded|complete/.test($doc.readyState);
      }

      var bodyDone = isBodyLoaded();
      if (bodyDone) {
        callback();
        return;
      }
      function onBodyDone(){
        if (!bodyDone) {
          bodyDone = true;
          callback();
          if ($doc.removeEventListener) {
            $doc.removeEventListener($intern_17, onBodyDone, false);
          }
          if (onBodyDoneTimerId) {
            clearInterval(onBodyDoneTimerId);
          }
        }
      }

      if ($doc.addEventListener) {
        $doc.addEventListener($intern_17, onBodyDone, false);
      }
      var onBodyDoneTimerId = setInterval(function(){
        if (isBodyLoaded()) {
          onBodyDone();
        }
      }
      , 50);
    }

    function installCode(code){
      function removeScript(body, element){
      }

      var docbody = getInstallLocation();
      var doc = getInstallLocationDoc();
      var script;
      if (navigator.userAgent.indexOf($intern_18) > -1 && window.JSON) {
        var scriptFrag = doc.createDocumentFragment();
        scriptFrag.appendChild(doc.createTextNode($intern_19));
        for (var i = 0; i < code.length; i++) {
          var c = window.JSON.stringify(code[i]);
          scriptFrag.appendChild(doc.createTextNode(c.substring(1, c.length - 1)));
        }
        scriptFrag.appendChild(doc.createTextNode($intern_20));
        script = doc.createElement($intern_21);
        script.language = $intern_22;
        script.appendChild(scriptFrag);
        docbody.appendChild(script);
        removeScript(docbody, script);
      }
       else {
        for (var i = 0; i < code.length; i++) {
          script = doc.createElement($intern_21);
          script.language = $intern_22;
          script.text = code[i];
          docbody.appendChild(script);
          removeScript(docbody, script);
        }
      }
    }

    org_placebooks_PlaceBookEditor.onScriptDownloaded = function(code){
      setupWaitForBodyLoad(function(){
        installCode(code);
      }
      );
    }
    ;
    sendStats($intern_23, $intern_24);
    var script = $doc.createElement($intern_21);
    script.src = filename;
    $doc.getElementsByTagName($intern_25)[0].appendChild(script);
  }

  org_placebooks_PlaceBookEditor.__startLoadingFragment = function(fragmentFile){
    return computeUrlForResource(fragmentFile);
  }
  ;
  org_placebooks_PlaceBookEditor.__installRunAsyncCode = function(code){
    var docbody = getInstallLocation();
    var script = getInstallLocationDoc().createElement($intern_21);
    script.language = $intern_22;
    script.text = code;
    docbody.appendChild(script);
  }
  ;
  function processMetas(){
    var metaProps = {};
    var propertyErrorFunc;
    var onLoadErrorFunc;
    var metas = $doc.getElementsByTagName($intern_26);
    for (var i = 0, n = metas.length; i < n; ++i) {
      var meta = metas[i], name = meta.getAttribute($intern_27), content;
      if (name) {
        name = name.replace($intern_28, $intern_14);
        if (name.indexOf($intern_29) >= 0) {
          continue;
        }
        if (name == $intern_30) {
          content = meta.getAttribute($intern_31);
          if (content) {
            var value, eq = content.indexOf($intern_32);
            if (eq >= 0) {
              name = content.substring(0, eq);
              value = content.substring(eq + 1);
            }
             else {
              name = content;
              value = $intern_14;
            }
            metaProps[name] = value;
          }
        }
         else if (name == $intern_33) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              propertyErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_35);
            }
          }
        }
         else if (name == $intern_36) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              onLoadErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_37);
            }
          }
        }
      }
    }
    __gwt_getMetaProperty = function(name){
      var value = metaProps[name];
      return value == null?null:value;
    }
    ;
    __propertyErrorFunction = propertyErrorFunc;
    org_placebooks_PlaceBookEditor.__errFn = onLoadErrorFunc;
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf($intern_38);
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf($intern_39);
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf($intern_40, Math.min(queryIndex, hashIndex));
      return slashIndex >= 0?path.substring(0, slashIndex + 1):$intern_14;
    }

    function ensureAbsoluteUrl(url){
      if (url.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc.createElement($intern_41);
        img.src = url + $intern_42;
        url = getDirectoryOfFile(img.src);
      }
      return url;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty($intern_43);
      if (metaVal != null) {
        return metaVal;
      }
      return $intern_14;
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc.getElementsByTagName($intern_21);
      for (var i = 0; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf($intern_44) != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return $intern_14;
    }

    function tryBaseTag(){
      var baseElements = $doc.getElementsByTagName($intern_45);
      if (baseElements.length > 0) {
        return baseElements[baseElements.length - 1].href;
      }
      return $intern_14;
    }

    function isLocationOk(){
      var loc = $doc.location;
      return loc.href == loc.protocol + $intern_46 + loc.host + loc.pathname + loc.search + loc.hash;
    }

    var tempBase = tryMetaTag();
    if (tempBase == $intern_14) {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == $intern_14) {
      tempBase = tryBaseTag();
    }
    if (tempBase == $intern_14 && isLocationOk()) {
      tempBase = getDirectoryOfFile($doc.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    return tempBase;
  }

  function computeUrlForResource(resource){
    if (resource.match(/^\//)) {
      return resource;
    }
    if (resource.match(/^[a-zA-Z]+:\/\//)) {
      return resource;
    }
    return org_placebooks_PlaceBookEditor.__moduleBase + resource;
  }

  function getCompiledCodeFilename(){
    var answers = [];
    var softPermutationId;
    function unflattenKeylistIntoAnswers(propValArray, value){
      var answer = answers;
      for (var i = 0, n = propValArray.length - 1; i < n; ++i) {
        answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
      }
      answer[propValArray[n]] = value;
    }

    var values = [];
    var providers = [];
    function computePropValue(propName){
      var value = providers[propName](), allowedValuesMap = values[propName];
      if (value in allowedValuesMap) {
        return value;
      }
      var allowedValuesList = [];
      for (var k in allowedValuesMap) {
        allowedValuesList[allowedValuesMap[k]] = k;
      }
      if (__propertyErrorFunc) {
        __propertyErrorFunc(propName, allowedValuesList, value);
      }
      throw null;
    }

    providers[$intern_47] = function(){
      var args = location.search;
      var start = args.indexOf($intern_47);
      if (start >= 0) {
        var value = args.substring(start);
        var begin = value.indexOf($intern_32) + 1;
        var end = value.indexOf($intern_48);
        if (end == -1) {
          end = value.length;
        }
        return value.substring(begin, end);
      }
      var ua = navigator.userAgent.toLowerCase();
      if (ua.indexOf($intern_49) != -1 || ua.indexOf($intern_50) != -1) {
        return $intern_51;
      }
       else if (ua.indexOf($intern_52) != -1) {
        return $intern_53;
      }
       else if (ua.indexOf($intern_54) != -1 || ua.indexOf($intern_51) != -1) {
        var dpi = 160;
        var width = $wnd.screen.width / dpi;
        var height = $wnd.screen.height / dpi;
        var size = Math.sqrt(width * width + height * height);
        return size < 6?$intern_51:$intern_53;
      }
      return $intern_55;
    }
    ;
    values[$intern_47] = {desktop:0, mobile:1, tablet:2};
    providers[$intern_56] = function(){
      var locale = null;
      var rtlocale = $intern_57;
      try {
        if (!locale) {
          var queryParam = location.search;
          var qpStart = queryParam.indexOf($intern_58);
          if (qpStart >= 0) {
            var value = queryParam.substring(qpStart + 7);
            var end = queryParam.indexOf($intern_48, qpStart);
            if (end < 0) {
              end = queryParam.length;
            }
            locale = queryParam.substring(qpStart + 7, end);
          }
        }
        if (!locale) {
          locale = __gwt_getMetaProperty($intern_56);
        }
        if (!locale) {
          locale = $wnd[$intern_59];
        }
        if (locale) {
          rtlocale = locale;
        }
        while (locale && !__gwt_isKnownPropertyValue($intern_56, locale)) {
          var lastIndex = locale.lastIndexOf($intern_60);
          if (lastIndex < 0) {
            locale = null;
            break;
          }
          locale = locale.substring(0, lastIndex);
        }
      }
       catch (e) {
        alert($intern_61 + e);
      }
      $wnd[$intern_59] = rtlocale;
      return locale || $intern_57;
    }
    ;
    values[$intern_56] = {cy:0, 'default':1, en:2};
    providers[$intern_62] = function(){
      var ua = navigator.userAgent.toLowerCase();
      var makeVersion = function(result){
        return parseInt(result[1]) * 1000 + parseInt(result[2]);
      }
      ;
      if (function(){
        return ua.indexOf($intern_63) != -1;
      }
      ())
        return $intern_63;
      if (function(){
        return ua.indexOf($intern_64) != -1;
      }
      ())
        return $intern_65;
      if (function(){
        return ua.indexOf($intern_66) != -1 && $doc.documentMode >= 9;
      }
      ())
        return $intern_67;
      if (function(){
        return ua.indexOf($intern_66) != -1 && $doc.documentMode >= 8;
      }
      ())
        return $intern_68;
      if (function(){
        var result = /msie ([0-9]+)\.([0-9]+)/.exec(ua);
        if (result && result.length == 3)
          return makeVersion(result) >= 6000;
      }
      ())
        return $intern_69;
      if (function(){
        return ua.indexOf($intern_70) != -1;
      }
      ())
        return $intern_71;
      return $intern_72;
    }
    ;
    values[$intern_62] = {gecko1_8:0, ie6:1, ie8:2, ie9:3, opera:4, safari:5};
    __gwt_isKnownPropertyValue = function(propName, propValue){
      return propValue in values[propName];
    }
    ;
    org_placebooks_PlaceBookEditor.__getPropMap = function(){
      var result = {};
      for (var key in values) {
        if (values.hasOwnProperty(key)) {
          result[key] = computePropValue(key);
        }
      }
      return result;
    }
    ;
    org_placebooks_PlaceBookEditor.__computePropValue = computePropValue;
    $wnd.__gwt_activeModules[$intern_4].bindings = org_placebooks_PlaceBookEditor.__getPropMap;
    sendStats($intern_0, $intern_73);
    if (isHostedMode()) {
      return computeUrlForResource($intern_74);
    }
    var strongName;
    try {
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_63], $intern_75);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_63], $intern_75);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_63], $intern_75);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_63], $intern_75 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_63], $intern_75 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_63], $intern_75 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_63], $intern_75 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_63], $intern_75 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_63], $intern_75 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_65], $intern_79);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_65], $intern_79);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_65], $intern_79);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_65], $intern_79 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_65], $intern_79 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_65], $intern_79 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_65], $intern_79 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_65], $intern_79 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_65], $intern_79 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_65], $intern_80);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_65], $intern_80);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_65], $intern_80);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_65], $intern_80 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_65], $intern_80 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_65], $intern_80 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_65], $intern_80 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_65], $intern_80 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_65], $intern_80 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_67], $intern_81);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_67], $intern_81);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_67], $intern_81);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_67], $intern_81 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_67], $intern_81 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_67], $intern_81 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_67], $intern_81 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_67], $intern_81 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_67], $intern_81 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_63], $intern_82);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_63], $intern_82);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_63], $intern_82);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_63], $intern_82 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_63], $intern_82 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_63], $intern_82 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_63], $intern_82 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_63], $intern_82 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_63], $intern_82 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_69], $intern_83);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_69], $intern_83);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_69], $intern_83);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_69], $intern_83 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_69], $intern_83 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_69], $intern_83 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_69], $intern_83 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_69], $intern_83 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_69], $intern_83 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_68], $intern_84);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_68], $intern_84);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_68], $intern_84);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_68], $intern_84 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_68], $intern_84 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_68], $intern_84 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_68], $intern_84 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_68], $intern_84 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_68], $intern_84 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_69], $intern_85);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_69], $intern_85);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_69], $intern_85);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_69], $intern_85 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_69], $intern_85 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_69], $intern_85 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_69], $intern_85 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_69], $intern_85 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_69], $intern_85 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_71], $intern_86);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_71], $intern_86);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_71], $intern_86);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_71], $intern_86 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_71], $intern_86 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_71], $intern_86 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_71], $intern_86 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_71], $intern_86 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_71], $intern_86 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_67], $intern_87);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_67], $intern_87);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_67], $intern_87);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_67], $intern_87 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_67], $intern_87 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_67], $intern_87 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_67], $intern_87 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_67], $intern_87 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_67], $intern_87 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_68], $intern_88);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_68], $intern_88);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_68], $intern_88);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_68], $intern_88 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_68], $intern_88 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_68], $intern_88 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_57, $intern_68], $intern_88 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_57, $intern_68], $intern_88 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_57, $intern_68], $intern_88 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_71], $intern_89);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_71], $intern_89);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_71], $intern_89);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_71], $intern_89 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_71], $intern_89 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_71], $intern_89 + $intern_76);
      unflattenKeylistIntoAnswers([$intern_55, $intern_78, $intern_71], $intern_89 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_51, $intern_78, $intern_71], $intern_89 + $intern_77);
      unflattenKeylistIntoAnswers([$intern_53, $intern_78, $intern_71], $intern_89 + $intern_77);
      strongName = answers[computePropValue($intern_47)][computePropValue($intern_56)][computePropValue($intern_62)];
      var idx = strongName.indexOf($intern_90);
      if (idx != -1) {
        softPermutationId = parseInt(strongName.substring(idx + 1), 10);
        strongName = strongName.substring(0, idx);
      }
    }
     catch (e) {
    }
    org_placebooks_PlaceBookEditor.__softPermutationId = softPermutationId;
    return computeUrlForResource(strongName + $intern_91);
  }

  function loadExternalStylesheets(){
    if (!$wnd.__gwt_stylesLoaded) {
      $wnd.__gwt_stylesLoaded = {};
    }
    function installOneStylesheet(stylesheetUrl){
      if (!__gwt_stylesLoaded[stylesheetUrl]) {
        var l = $doc.createElement($intern_92);
        l.setAttribute($intern_93, $intern_94);
        l.setAttribute($intern_95, computeUrlForResource(stylesheetUrl));
        $doc.getElementsByTagName($intern_25)[0].appendChild(l);
        __gwt_stylesLoaded[stylesheetUrl] = true;
      }
    }

    sendStats($intern_96, $intern_1);
    installOneStylesheet($intern_97);
    sendStats($intern_96, $intern_98);
  }

  processMetas();
  org_placebooks_PlaceBookEditor.__moduleBase = computeScriptBase();
  activeModules[$intern_4].moduleBase = org_placebooks_PlaceBookEditor.__moduleBase;
  var filename = getCompiledCodeFilename();
  if ($wnd) {
    $wnd.__gwt_activeModules[$intern_4].canRedirect = true;
  }
  var devModeKey = $intern_99;
  var devModeUrl = $wnd.sessionStorage[devModeKey];
  if (devModeUrl && !$wnd[devModeKey]) {
    $wnd[devModeKey] = true;
    var script = $doc.createElement($intern_21);
    $wnd[devModeKey + $intern_100] = computeScriptBase();
    script.src = devModeUrl;
    var head = $doc.getElementsByTagName($intern_25)[0];
    head.insertBefore(script, head.firstElementChild || head.children[0]);
    return false;
  }
  loadExternalStylesheets();
  sendStats($intern_0, $intern_98);
  installScript(filename);
  return true;
}

org_placebooks_PlaceBookEditor.succeeded = org_placebooks_PlaceBookEditor();
