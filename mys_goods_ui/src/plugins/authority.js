import store from '@/store/index';

export function verify(auth) {
  const authList = store.state.authList;
  return authList[auth];
}