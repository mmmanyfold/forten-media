(ns forten-media.components.footer)

(defn footer-large [number]
  [:div.footer-wrap.w-80
   [:div.footer
    [:div [:a {:href "/#/"}
           "< table of contents"]]
    [:div {:style {:text-align "center"}} number]
    [:div ""]]])

(defn footer-small [number]
  [:div.footer-wrap.w-100-small
   [:div.footer
    [:div [:a {:href "/#/"}
           "< table of contents"]]
    [:div {:style {:text-align "center"}} number]
    [:div ""]]])
