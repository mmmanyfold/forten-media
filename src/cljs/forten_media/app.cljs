(ns forten-media.app
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]
            [forten-media.views.home :refer [home-view]]
            [forten-media.views.our-work :refer [our-work-view]]
            [forten-media.views.our-difference :refer [our-difference-view]]))
;; main

(defn- views [view-name]
  (case view-name
    :home-view [home-view]
    :our-work-view [our-work-view]
    :our-difference-view [our-difference-view]
    [:div]))

(defn show-view [view-name]
  [views view-name])

(defn main-view []
  (let [active-view (re-frame/subscribe [::subs/active-view])]
    [re-com/v-box
     :height "100%"
     :justify :center
     :children [[views @active-view]
                [:div.crease.hide-small]]]))
