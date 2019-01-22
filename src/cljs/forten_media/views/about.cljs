(ns forten-media.views.about
  (:require [forten-media.components.footer :refer [footer-large footer-small]]))


(defn about-view []
  [:div.wrapper.animated.fadeIn
   [:div.left
    [:div.w-80.text.w-100-small
     [:h1 [:span "ABOUT"] " US"]
     [:p "Forten Media is an Atlanta-based creative digital media agency founded by filmmakers and focused on storytelling. Our process is collaborative and relationship-based, and it allows us to produce the kind of work that brings our clients back to us again and again. Our goal is to create high-quality work that is bold and imaginative, by helping clients to establish a truly personal relationship with their audience."]
     [footer-large "10"]]]
   [:div.right
    [:div.display.w-70.w-100-small
     [:h3 "photo"]
     [:h3 "forten media"]
     [:h2 "the team, " [:span "2018"]]
     [:img {:src "../img/about.png"}]
     [footer-small "10"]]]])
