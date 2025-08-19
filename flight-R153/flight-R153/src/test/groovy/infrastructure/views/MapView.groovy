package infrastructure.views

import geb.Module
import geb.Page

/**
 * Created by alabdullahwi on 5/29/2015.
 */
class MapView extends Page {
   static content =  {
      welcomePopup         { $('div', id: 'welcomeWindowEmitters')      }
      map                  { $('div', id: 'canvas-map')     }
      dropdown             { module Dropdowns  }
      popup                { module Popups }
   }

}


class Dropdowns  extends Module {

   static content = {
      emissionType { $('select', id: 'emissionsType') }
      tribal       { $('div#tribeSelection select') }
   }

}

class Popups extends Module {
   static content =  {
      welcomeEmitters { module WelcomePopupEmitters }
   }

}

class WelcomePopupEmitters extends Module {

   static content = {
      self { $('div', id: 'welcomeWindowEmitters') }
      usaMap { self.$('td > div.large  img')  }
   }

}
