(ns pixel-ship-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [pixel-ships.core :as psc :refer :all]
            [pixel-ships.bollinger :as bollinger :refer :all]))

(declare create-pixel-ship-quil draw-rect)

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb 1.0)
  ; create ship and put it into state
  {:pixel-ship (create-pixel-ship-quil)})

(defn update-state [state]
  state)

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0.9)
  (let [hull-pixels ((comp :hull :pixels) (:pixel-ship state))
        solid-pixels ((comp :solid :pixels) (:pixel-ship state))
        cockpit-pixels ((comp :cockpit :pixels) (:pixel-ship state))]
    (q/with-translation [200
                         (- (q/height) 200)]
      (doseq [pixel hull-pixels]
        (draw-rect pixel))
      (doseq [pixel solid-pixels]
        (draw-rect pixel))
      (doseq [pixel cockpit-pixels]
        (draw-rect pixel))
    )))

(defn key-press [state event]
  (if (= (q/raw-key ) \space )
    (assoc state :pixel-ship (create-pixel-ship-quil))
    state))

(defn -main
  []
  (q/sketch
    :title "An example of a pixel-ship in Quil"
    :size [800 600]
    :setup setup
    ;:update update-state
    :draw draw-state
    :key-typed key-press
    :features [:keep-on-top]
    :middleware [m/fun-mode] ))

;(-main)

(defn create-pixel-ship-quil []
  (psc/color-pixel-ship (psc/create-pixel-ship (assoc bollinger/model :seed (rand-int Integer/MAX_VALUE)))))

(def p-per-c 20)

(defn draw-rect [{:keys [x y color]}]
  (q/fill (:h color) (:s color) (:v color))
  (q/rect (- (* x p-per-c) (* 6 p-per-c)) (- (* y p-per-c) (* 6 p-per-c)) p-per-c p-per-c))

