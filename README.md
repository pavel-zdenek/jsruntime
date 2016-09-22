Unification layer for Javascript injection and evaluation in two Android environments:

1. WebKit through WebView instance
2. Chrome V8 through @irbull’s amazing J2V8

The common API is modelled after WebView, the actual environments are being talked to through adapters.

Requires jpromise.
