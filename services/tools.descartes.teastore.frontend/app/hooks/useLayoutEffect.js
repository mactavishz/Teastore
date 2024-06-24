import { useEffect, useCallback } from 'react';

export function useLayoutEffect() {
  const resizing = useCallback(() => {
    let viewportwidth;
    let viewportheight;

    // the more standards compliant browsers (mozilla/netscape/opera/IE7) use
    // window.innerWidth and window.innerHeight

    if (typeof window.innerWidth != 'undefined') {
      viewportwidth = window.innerWidth;
      viewportheight = window.innerHeight;
    }
    // IE6 in standards compliant mode (i.e. with a valid doctype as the first
    // line in the document)
    else if (typeof document.documentElement != 'undefined'
      && typeof document.documentElement.clientWidth != 'undefined'
      && document.documentElement.clientWidth != 0) {
      viewportwidth = document.documentElement.clientWidth,
        viewportheight = document.documentElement.clientHeight
    }
    // older versions of IE
    else {
      viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
      viewportheight = document.getElementsByTagName('body')[0].clientHeight
    }

    $("#main").css(
      "min-height",
      (viewportheight - 75 * 2)
      + "px");

    if ($('#navbarbutton').is(':visible')) {
      hiding();
    } else {
      showing();
    }
  }, []);

  const hiding = useCallback(() => {
    $('.sidebar').hide();
    $('.category-item').each(function () {
      $(this).appendTo('ul.headnavbarlist');
    })
  }, []);

  const showing = useCallback(() => {
    $('.sidebar').show();
    $('.category-item').each(function () {
      $(this).appendTo('ul.nav-sidebar');
    })
  }, []);

  useEffect(() => {
    resizing();
    window.addEventListener('resize', resizing);

    return () => {
      window.removeEventListener('resize', resizing);
    };
  }, [resizing]);

  return null; // This hook doesn't need to return anything
}