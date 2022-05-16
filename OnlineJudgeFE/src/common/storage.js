const localStorage = window.localStorage

export default {
  name: 'storage',


  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value))
  },


  get(key) {
    return JSON.parse(localStorage.getItem(key)) || null
  },


  remove(key) {
    localStorage.removeItem(key)
  },

  clear() {
    localStorage.clear()
  }
}
