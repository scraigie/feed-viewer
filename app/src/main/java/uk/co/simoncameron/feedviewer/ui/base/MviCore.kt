package uk.co.simoncameron.feedviewer.ui.base

interface State
interface Action
interface Effect

typealias Reducer<S> = (S) -> S