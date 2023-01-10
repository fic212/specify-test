const publicPath = './output/light';

const colorRules = [
  {
    name: 'Light Theme / Colors',
    path: `${publicPath}/`,
    filter: {
      types: ['color'],
    },
    parsers: [
      {
        name: 'sort-by',
        options: {
          keys: ['name'],
        },
      },
      {
        name: 'to-style-dictionary',
        options: {
          formatTokens: {
            colorFormat: {
                format: 'hex8',
            },
          },
        },
      },
    ],
  },
];

/*
 * Finally exports the configuration
 */
module.exports = {
  // Find more about how to target a Specify repository at: https://specifyapp.com/developers/api#heading-parameters
  rules: [...colorRules],
};
