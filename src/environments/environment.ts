/**
 * Developpement : « ng serve » relaie /api vers le backend Spring grace a
 * proxy.conf.json. L'URL reste relative, exactement comme en production,
 * ce qui supprime toute question d'origine croisee.
 */
export const environment = {
  production: false,
  apiUrl: '/api',
};
