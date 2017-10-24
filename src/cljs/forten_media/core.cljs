(ns forten-media.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [forten-media.events :as events]
            [forten-media.routes :as routes]
            [forten-media.views :as views]
            [forten-media.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
