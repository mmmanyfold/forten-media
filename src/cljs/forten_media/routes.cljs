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

  (defroute "/about" []
    (re-frame/dispatch [::events/set-active-view :about-view]))

  (defroute "/connect" []
    (re-frame/dispatch [::events/set-active-view :connect-view]))


  ;; --------------------
  (hook-browser-navigation!))
