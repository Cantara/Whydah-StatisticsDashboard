import Crud from "@/store/basestore"
const crud = new Crud('api')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,

    async get_usersession_status({ dispatch, state, commit }, { callbackfunc }) {
        await dispatch('get', { affixPath: `/status`, key: "get_usersession_status_result" })
        if (state.storage.get_usersession_status_result) {
            if (callbackfunc) {
                callbackfunc(state.storage.get_usersession_status_result);
            }
            commit("SET_REMOVE_KEY", "get_usersession_status_result")
        }
    }
}

export const getters = {
    ...crud.getters
}
