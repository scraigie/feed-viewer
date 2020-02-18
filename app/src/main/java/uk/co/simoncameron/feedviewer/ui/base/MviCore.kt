package uk.co.simoncameron.feedviewer.ui.base

interface State
interface Action

typealias Reducer<S> = (S) -> S