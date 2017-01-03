(ns sentinel.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn title []
  [re-com/v-box
   :align :center
   :children [[:img {:src   "img/sentinel-logo.png"
                     :height "100px"
                     :width "100px"}]
              [re-com/title
               :label (str "Sentinel")
               :level :level1
               :style {:margin-top "0px"}]]])

(defn up? [status]
  (= status :up))

(defn server-box [name url]
  (let [status (re-frame/subscribe [:get-server-status [name url]])
        up? (up? @status)]
    [re-com/v-box
     :class (if up? "success-bg" "alert-bg")
     :size "auto"
     :align-self (if up? :end :stretch)
     :align :center
     :children [[re-com/title :label name :level (if up? :level3 :level1) :style {:color "white"}]]]))

(defn add-bar []
  [re-com/h-box
   :children [
              [re-com/input-text
               :placeholder "server name"
               :model ""
               :on-change #(re-frame/dispatch [:server-name-changed %])]
              [re-com/input-text
               :placeholder "server url"
               :model ""
               :on-change #(re-frame/dispatch [:server-url-changed %])]
              [re-com/button
               :label "add"
               :on-click #(re-frame/dispatch [:add-server])]
              [re-com/gap :size "50px"]
              [re-com/button
               :label "@Consul"
               :on-click #(re-frame/dispatch [:read-consul])]
              ]]
  )

(defn servers []
  (let [servers (re-frame/subscribe [:get-servers])
        server-groups (partition-all 10 @servers)]
    [re-com/v-box
     :max-width "100%"
     :class "success-bg"
     :children [(for [server-group server-groups]
                  [re-com/h-box
                   :height "100px"
                   :children [(for [[server-name server-url] server-group]
                                [server-box server-name server-url])]])]]))

(defn main-panel []
  (fn []
    [re-com/v-box
     :height "100%"
     :children [[title]
                [add-bar]
                [re-com/gap :size "20px"]
                [servers]]]))
