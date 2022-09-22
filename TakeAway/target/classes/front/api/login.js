function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function registryApi(data) {
    return $axios({
        'url': '/user/registry',
        'method': 'post',
        data
    })
}

function loginoutApi() {
  return $axios({
    'url': '/user/logout',
    'method': 'post',
  })
}

  