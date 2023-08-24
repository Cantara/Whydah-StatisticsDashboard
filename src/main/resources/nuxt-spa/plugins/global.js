export default (context, inject) => {
  
  const global = context => ({

    
  
  });

  //@Appratiff, do you think it is an appropriate place to put all the constant values?
  inject("global", global(context));
};
