(function (factory, window) {
	/*globals define, module, require*/
	// define an AMD module that relies on 'leaflet'
	if (typeof define === 'function' && define.amd) {
		define(['leaflet'], factory);
		// define a Common JS module that relies on 'leaflet'
	} else if (typeof exports === 'object') {
		module.exports = factory(require('leaflet'));
	}
	// attach your plugin to the global 'L' variable
	if (typeof window !== 'undefined' && window.L) {
		factory(window.L);
	}
}(function (L) {
		'use strict';
		var EggrtPoly = {
			/**
			 * Returns an instance property by key. Has the ability to set an object if the property does not exist
			 * @param key:string
			 * @param value:object(optional)
			 */
			getObject: function (key, value) {
				var _this = this;
				if (!_this[key] && value) {
					this.setObject(key, value);
				}
				return _this[key];
			},
			/**
			 * Sets an instance property
			 * @param key:string
			 * @param value:object
			 */
			setObject: function (key, value) {
				this[key] = value;
				return this;
			}
		}
		if (L.Polygon) {
			L.Polygon.include(EggrtPoly);
		}
		var EggrtMarker = {
			setPosition: function (el, point) {
				console.log("called EggrtMarker setPosition");
			},
			setVisible: function () {
				console.log("called EggrtMarker setVisible");
			}
		}
		if (L.Marker) {
			L.Marker.include(EggrtMarker);
		}
	},
	window
));
