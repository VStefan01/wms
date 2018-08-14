var Selector = {
                    BODY                 : 'body',
                    BRAND_MINIMIZER      : '.brand-minimizer',
                    NAV_DROPDOWN_TOGGLE  : '.nav-dropdown-toggle',
                    NAV_DROPDOWN_ITEMS   : '.nav-dropdown-items',
                    NAV_LINK             : '.nav-link',
                    NAVIGATION_CONTAINER : '.sidebar-nav',
                    NAVIGATION           : '.sidebar-nav > .nav',
                    SIDEBAR              : '.sidebar',
                    SIDEBAR_MINIMIZER    : '.sidebar-minimizer',
                    SIDEBAR_TOGGLER      : '.sidebar-toggler'
                    };
var ClassName = {
                    ACTIVE              : 'active',
                    BRAND_MINIMIZED     : 'brand-minimized',
                    NAV_DROPDOWN_TOGGLE : 'nav-dropdown-toggle',
                    OPEN                : 'open',
                    SIDEBAR_FIXED       : 'sidebar-fixed',
                    SIDEBAR_MINIMIZED   : 'sidebar-minimized',
                    SIDEBAR_OFF_CANVAS  : 'sidebar-off-canvas'
                    };

var showSpinner = function(){
                    $('.ajax').show();
                }
var hideSpinner = function(){
                    $('.ajax').hide();
                }

var successMessage = function (args, title, text){
                    if (!args.validationFailed){
                          toastr.success(text,title);
                    }
                    hideSpinner();
                }

var errorMessage = function (title, text){
                      toastr.error(text,title);
                }
