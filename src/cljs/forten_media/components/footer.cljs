(ns forten-media.components.footer)

(defn footer-component [number]
  [:div.footer-wrap.w-80.w-100-small
   [:div.footer
    [:div [:a {:href "/#/"}
           "< table of contents"]]
    [:div {:style {:text-align "center"}} number]
    [:div ""]]])
