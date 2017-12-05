(ns forten-media.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]
            [forten-media.events :as events]))


(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
    (re-frame/dispatch [::events/set-active-view :home-view]))

  (defroute "/our-work" []
    (re-frame/dispatch [::events/set-active-view :our-work-view]))

  (defroute "/our-difference" []
    (re-frame/dispatch [::events/set-active-view :our-difference-view]))


  ;; --------------------
  (hook-browser-navigation!))
