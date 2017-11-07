(ns forten-media.views.home
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn home-view []
  [:div.wrapper
    [:div.left
     [:div.display
      [:img {:src "../img/demo-reel.png"}]
      [:h2 "demo reel, " [:span "2017"]]
      [:h3 "forten media, llc"]
      [:h3 "video"]
      [:h3 "1920x804"]]]
    [:div.right]])
