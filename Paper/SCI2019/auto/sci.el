(TeX-add-style-hook
 "sci"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("graphicx" "dvipdfmx")))
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
   (TeX-run-style-hooks
    "latex2e"
    "ujarticle"
    "ujarticle10"
    "amsmath"
    "graphicx"
    "bmpsize"
    "here")
   (LaTeX-add-labels
    "fig:CsMfg"
    "fig:bid-provider-single"
    "fig:request"
    "シングル-目的関数"
    "シングル-容量制約"
    "シングル-組合せ制約"
    "シングル-提供者数制約"
    "シングル-入札勝者数制約x"
    "シングル-入札勝者数制約y"
    "シングル-予算制約"
    "シングル-決定変数"
    "シングル-取引価格"
    "fig:bid-provider-double"
    "ダブル-目的関数"
    "ダブル-容量制約"
    "ダブル-組合せ制約"
    "ダブル-提供者数制約"
    "ダブル-入札勝者数制約x"
    "ダブル-予算制約"
    "ダブル-決定変数"
    "取引価格"
    "合計時間"
    "tab:profit"
    "tab:trade"
    "tab:request_rate"
    "tab:provide_rate")
   (LaTeX-add-bibliographies))
 :latex)

