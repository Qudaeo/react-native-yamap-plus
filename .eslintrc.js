module.exports = {
  root: true,
  env: {
    es6: true,
    'react-native/react-native': true,
  },
  extends: [
    'plugin:wirthus/react-native',
  ],
  plugins: ['wirthus'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    project: 'tsconfig.json',
    tsconfigRootDir: __dirname
  },
  rules: {
    'newline-before-return': 'off',

    '@typescript-eslint/no-explicit-any': 'off',
    '@typescript-eslint/no-floating-promises': 'warn',
    '@typescript-eslint/no-unsafe-assignment': 'off',

    'react-native/no-color-literals': 'off',
    'react-native/no-inline-styles': 'error',
    'react-native/no-raw-text': 'off',
    'react-native/no-single-element-style-arrays': 'error',
    'react-native/no-unused-styles': 'warn',
    'react-native/split-platform-components': 'off',
  }
};
