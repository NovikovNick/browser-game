import setting from "../config"


export function changePlayerName(formData) {
    const settings = {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(formData)
    };
    return ajax(setting.HOST + '/player', settings)
}

/**
 * Requests a URL, returning a promise
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 *
 * @return {Promise}           The request promise
 */
function ajax(url, options) {

    options.headers = options.headers || {};
    options.headers['TIMEZONE_OFFSET'] = new Date().getTimezoneOffset();

    return new Promise((resolve, reject) => {
        fetch(url, options)
            .then(response => {
                switch (response.status) {
                    case 200:
                        return new Promise(() => response.json().then((json) => {

                            if (json.message) {
                                console.info(json.message.payload);
                            }
                            resolve(json)
                        }));
                    case 400:
                        return new Promise(() => response.json().then((json) => {
                            if (json.message) {
                                console.error(json.message.payload);
                            }
                            reject(json)
                        }));
                    case 403:
                        window.location = "/signin";
                        break;
                    default:
                        console.error(response.status);
                }
            })
            .catch(error => console.error(error))
    });
}