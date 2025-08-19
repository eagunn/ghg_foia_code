 /*!
 * jQuery Bing Map 1.0
 * http://jquery-bing-maps.googlecode.com
 * Copyright (c) 2010 - 2012 Johan Säll Larsson
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 *
 * Depends:
 *		jquery.ui.bmap.js
 */
( function($) {

	$.extend($.ui.gmap.prototype, {
		
		/**
		 * Adds a shape to the map
		 * @param type:string Polygon, Polyline, Rectangle, Circle
		 * @param options:object
		 * @return object
		 */
		addShape: function(a, b) {
			var self = this;
			$.each(b.paths, function(i, path) {
				b.paths[i] = self._latLng(path);
			});
			var shape = new Microsoft.Maps[a](b.paths, b);
			this.get('overlays > ' + a, []).push(shape);
			this.get('map').entities.push(shape);
			return $(shape);
		}
	
	});
	
} (jQuery) );