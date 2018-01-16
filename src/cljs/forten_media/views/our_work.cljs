(ns forten-media.views.our-work
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn our-work-view []
  [:div.wrapper
   [:div.left
    [:div.w-80.text
     [:h1 "OUR " [:span "WORK"]]
     [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent faucibus lacus quis lobortis feugiat. Duis in pharetra sapien. Sed quis velit quis nibh rutrum auctor. Etiam turpis arcu, varius eget ante et, tincidunt luctus risus. In ac dapibus tortor, fringilla posuere augue. Nullam nunc metus, gravida a libero ut, commodo suscipit massa. Phasellus ultricies placerat lectus, at aliquet leo convallis eget. Fusce non mattis elit. Cras id scelerisque lectus. Proin orci libero, scelerisque nec commodo at, aliquet nec ex. Proin feugiat et tortor hendrerit efficitur. Integer in lectus lacus. Nulla ac cursus massa. Sed massa augue, ullamcorper pellentesque sapien sed, viverra egestas metus. Maecenas aliquet magna tellus, a semper dolor malesuada a."]
     [:div.footer-wrap.w-80
      [:div.footer
       [:div [:a {:href "/#/"}
              "< table of contents"]]
       [:div {:style {:text-align "center"}} "14"]
       [:div ""]]]]]
   [:div.right
    [:div.display.w-40
     [:img {:src "../img/recipe.png"}]
     [:h2 "burger bowl recipe, " [:span "2017"]]
     [:h3 "form star living"]
     [:h3 "video"]
     [:h3 "1080x1080"]]]])
